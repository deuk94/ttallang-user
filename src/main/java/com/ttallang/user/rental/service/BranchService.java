package com.ttallang.user.rental.service;

import com.ttallang.user.commonModel.Branch;
import com.ttallang.user.commonModel.Bicycle;
import com.ttallang.user.commonModel.Payment;
import com.ttallang.user.commonModel.Rental;
import com.ttallang.user.rental.repository.BicycleRepository;
import com.ttallang.user.rental.repository.BranchRepository;
import com.ttallang.user.rental.repository.PaymentRepository;
import com.ttallang.user.rental.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BranchService {

    private final BranchRepository branchRepository;
    private final BicycleRepository bicycleRepository;
    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;

    @Autowired
    public BranchService(BranchRepository branchRepository, BicycleRepository bicycleRepository,
        RentalRepository rentalRepository, PaymentRepository paymentRepository) {
        this.branchRepository = branchRepository;
        this.bicycleRepository = bicycleRepository;
        this.rentalRepository = rentalRepository;
        this.paymentRepository = paymentRepository;
    }

    // branch_status가 1인 대여소만 가져오는 메서드
    public List<Branch> getActiveBranches() {
        return branchRepository.findActiveBranches();
    }

    // 특정 위치에서 사용 가능한 자전거 수를 반환
    public int getAvailableBikesAtLocation(double latitude, double longitude) {
        double distance = 0.00001;
        return bicycleRepository.findByBikeCount(latitude, longitude, distance);
    }

    // 특정 위치 근처의 이용 가능한 자전거 목록을 반환
    public List<Bicycle> getAvailableBikesList(double latitude, double longitude) {
        double distance = 0.00001;
        return bicycleRepository.findAvailableBike(latitude, longitude, distance);
    }

    // 자전거 대여 로직을 처리하는 메서드
    public String rentBicycle(int bicycleId, int customerId, String rentalBranch) {

        // 고객의 최신 결제 상태 확인
        Optional<Payment> latestPayment = paymentRepository.findLatestPaymentByCustomerId(customerId);
        if (latestPayment.isPresent() && "0".equals(latestPayment.get().getPaymentStatus())) {
            return "결제가 완료되지 않았습니다. 결제를 완료한 후 대여할 수 있습니다.";
        }

        // 고객이 이미 대여 중인지 확인
        List<Rental> activeRentals = rentalRepository.findByCustomerIdAndRentalEndDateIsNull(customerId);
        if (!activeRentals.isEmpty()) {
            return "반납되지 않은 대여가 있습니다. 새 자전거를 대여하기 전에 반납해 주세요.";
        }

        // 자전거의 대여 상태를 확인 후 대여 가능 상태로 변경
        Optional<Bicycle> bicycleOptional = bicycleRepository.findById(bicycleId);
        if (bicycleOptional.isPresent()) {
            Bicycle bicycle = bicycleOptional.get();
            if (!"0".equals(bicycle.getRentalStatus())) {
                return "이 자전거는 이미 대여 중입니다.";
            }
            bicycle.setRentalStatus("1"); // 대여 상태로 변경
            bicycleRepository.save(bicycle);
        } else {
            return "자전거를 찾을 수 없습니다.";
        }

        // 대여 정보 저장
        Rental rental = new Rental();
        rental.setBicycleId(bicycleId);
        rental.setCustomerId(customerId);
        rental.setRentalBranch(rentalBranch);
        rental.setRentalStartDate(LocalDateTime.now());
        rentalRepository.save(rental);
        return "대여가 성공적으로 완료되었습니다.";
    }

    // 자전거 반납 로직을 처리하는 메서드
    @Transactional
    public String returnBicycle(int customerId, double returnLatitude, double returnLongitude, boolean isCustomLocation) {
        // 고객의 현재 대여 기록 확인
        List<Rental> activeRentals = rentalRepository.findByCustomerIdAndRentalEndDateIsNull(customerId);
        if (activeRentals.isEmpty()) {
            return "반납할 대여 내역이 없습니다.";
        }

        Rental rental = activeRentals.get(0); // 활성화된 첫 번째 대여 기록 사용
        String returnBranchName = isCustomLocation ? "기타" : determineReturnBranch(returnLatitude, returnLongitude);

        // 반납 지점 및 시간 설정
        rental.setReturnBranch(returnBranchName);
        rental.setRentalEndDate(LocalDateTime.now());

        // 자전거 상태 업데이트
        Optional<Bicycle> bicycleOptional = bicycleRepository.findById(rental.getBicycleId());
        if (bicycleOptional.isPresent()) {
            Bicycle bicycle = bicycleOptional.get();
            bicycle.setRentalStatus("0"); // 반납 상태로 변경
            bicycle.setLatitude(returnLatitude);
            bicycle.setLongitude(returnLongitude);
            bicycleRepository.save(bicycle);
        } else {
            return "자전거를 찾을 수 없습니다.";
        }
        rentalRepository.save(rental);

        // 결제 정보 계산 및 저장
        calculateAndSavePayment(rental, customerId);
        return "반납이 성공적으로 완료되었습니다.";
    }

    // 반납 지점 결정 메서드
    private String determineReturnBranch(double returnLatitude, double returnLongitude) {
        List<Branch> nearbyBranches = branchRepository.findNearbyBranches(returnLatitude, returnLongitude, 0.00001);
        return nearbyBranches.isEmpty() ? "기타" : nearbyBranches.get(0).getBranchName();
    }

    // 결제 정보
    private void calculateAndSavePayment(Rental rental, int customerId) {
        // 결제 정보 객체 생성
        Payment payment = new Payment();
        payment.setRentalId(rental.getRentalId());
        payment.setCustomerId(customerId);
        payment.setPaymentAmount(null);
        payment.setPaymentStatus("0"); // 결제 전 상태

        paymentRepository.save(payment); // 결제 정보 저장
    }

    // 고객의 현재 대여 상태 확인 메서드
    public Optional<Bicycle> getCurrentRentalByCustomerId(int customerId) {
        List<Rental> rentals = rentalRepository.findByCustomerIdAndRentalEndDateIsNull(customerId);
        if (!rentals.isEmpty()) {
            int bicycleId = rentals.get(0).getBicycleId();
            return bicycleRepository.findById(bicycleId);
        }
        return Optional.empty();
    }
}
