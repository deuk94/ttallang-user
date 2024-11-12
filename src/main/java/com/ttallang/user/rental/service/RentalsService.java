package com.ttallang.user.rental.service;

import com.ttallang.user.commonModel.Bicycle;
import com.ttallang.user.commonModel.Payment;
import com.ttallang.user.commonModel.Rental;
import com.ttallang.user.rental.model.UseRental;
import com.ttallang.user.commomRepository.BicycleRepository;
import com.ttallang.user.commomRepository.RentalRepository;
import com.ttallang.user.commomRepository.PaymentRepository;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RentalsService {

    private final BicycleRepository bicycleRepository;
    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentsService paymentsService;

    @Autowired
    public RentalsService(BicycleRepository bicycleRepository, RentalRepository rentalRepository, PaymentRepository paymentRepository,
        PaymentsService paymentsService) {
        this.bicycleRepository = bicycleRepository;
        this.rentalRepository = rentalRepository;
        this.paymentRepository = paymentRepository;
        this.paymentsService = paymentsService;
    }

    // 자전거 대여
    public Map<String, Object> rentBicycle(int bicycleId, String rentalBranch, int customerId) {
        Map<String, Object> result = new HashMap<>();
        // 결제 상태 확인 - 미결제 상태가 있을 경우 예외 발생
        if (!paymentRepository.findByCustomerIdAndPaymentStatus(customerId, "0").isEmpty()) {
            result.put("rental", null);
            result.put("code", 403);
            result.put("msg", "결제되지 않은 자전거가 있습니다. 결제를 완료해 주세요.");
            return result;
        }

        // 대여 중인 자전거가 있는지 확인
        if (!rentalRepository.findByCustomerIdAndRentalEndDateIsNull(customerId).isEmpty()) {
            result.put("rental", null);
            result.put("code", 403);
            result.put("msg", "이미 대여 중인 자전거가 있습니다. 반납 후 새로운 대여가 가능합니다.");
            return result;
        }

        // 자전거 대여 상태 확인
        Bicycle bicycle = bicycleRepository.findById(bicycleId)
            .orElseThrow(() -> {
                result.put("msg", "자전거를 찾을 수 없습니다.");
                result.put("code", 404);
                result.put("rental", null);
                return null;
            });

        if (!"1".equals(bicycle.getRentalStatus()) || !"1".equals(bicycle.getBicycleStatus())) {
            result.put("rental", null);
            result.put("code", 400);
            result.put("msg", "이 자전거는 대여할 수 없는 상태입니다.");
            return result;
        }

        // 자전거 대여 가능 상태로 설정 및 저장
        bicycle.setRentalStatus("0");
        bicycleRepository.save(bicycle);

        // 대여 내역 저장
        Rental rental = new Rental();
        rental.setBicycleId(bicycleId);
        rental.setCustomerId(customerId);
        rental.setRentalBranch(rentalBranch);
        rental.setRentalStartDate(LocalDateTime.now());
        result.put("rental", rental);
        result.put("code", 200);
        result.put("msg", "렌탈 성공.");
        rentalRepository.save(rental);
        return result;
    }

    // 자전거 반납
    public Rental returnBicycle(double returnLatitude, double returnLongitude, boolean isCustomLocation, String returnBranchName, int customerId) {

        // 반납할 대여 내역 조회
        Rental rental = rentalRepository.findByCustomerIdAndRentalEndDateIsNull(customerId)
            .stream()
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("반납할 대여 내역이 없습니다."));

        // 자전거 상태 업데이트 및 반납 처리
        rental.setReturnBranch(returnBranchName);
        rental.setRentalEndDate(LocalDateTime.now());
        rentalRepository.save(rental);

        Bicycle bicycle = bicycleRepository.findById(rental.getBicycleId())
            .orElseThrow(() -> new IllegalArgumentException("자전거를 찾을 수 없습니다."));
        bicycle.setRentalStatus("1");
        bicycle.setLatitude(returnLatitude);
        bicycle.setLongitude(returnLongitude);

        if (isCustomLocation) {
            bicycle.setBicycleStatus("0");
        }

        bicycleRepository.save(bicycle);
        bicycleRepository.flush();

        // 결제 처리 로직
        paymentsService.calculateAndSavePayment(rental.getRentalId(), customerId);

        return rental;
    }

    // 현재 대여 중인 자전거 정보 가져오기
    public UseRental getCurrentRentalsByCustomerId(int customerId) {
        return rentalRepository.findCustomerIdAndRentalStatus(customerId);
    }

    // 현재 대여 중인 자전거 반환
    public Optional<Bicycle> getCurrentRentalByCustomerId(int customerId) {

        List<Rental> rentals = rentalRepository.findByCustomerIdAndRentalEndDateIsNull(customerId);
        if (!rentals.isEmpty()) {
            int bicycleId = rentals.get(0).getBicycleId();
            return bicycleRepository.findById(bicycleId);
        }
        return Optional.empty();
    }

    // 현황판을 위한 메서드
    public Map<String, Object> getRentalStatusForCustomer(int customerId) {
        Map<String, Object> rentalStatus = new HashMap<>();

        Rental currentRental = rentalRepository.findByCustomerIdAndRentalEndDateIsNull(customerId)
            .stream()
            .findFirst()
            .orElse(null);

        if (currentRental != null) {
            Optional<Bicycle> rentedBicycle = bicycleRepository.findById(currentRental.getBicycleId());
            if (rentedBicycle.isPresent()) {
                rentalStatus.put("bicycleName", rentedBicycle.get().getBicycleName());
                rentalStatus.put("rentalBranch", currentRental.getRentalBranch());
                rentalStatus.put("rentalStartDate", currentRental.getRentalStartDate());
                rentalStatus.put("currentLatitude", rentedBicycle.get().getLatitude());
                rentalStatus.put("currentLongitude", rentedBicycle.get().getLongitude());
                rentalStatus.put("code", 200);
                rentalStatus.put("msg", "대여 현황 조회 성공");
            } else {
                rentalStatus.put("code", 404);
                rentalStatus.put("msg", "대여 중인 자전거 정보를 찾을 수 없습니다.");
            }
        } else {
            rentalStatus.put("code", 404);
            rentalStatus.put("msg", "대여 중인 자전거가 없습니다.");
        }
        return rentalStatus;
    }
    // 현황판 반납 기능에 결제 로직을 포함한 새로운 메서드
    public Map<String, Object> completeReturn(double returnLatitude, double returnLongitude, boolean isCustomLocation, String returnBranchName, int customerId) {
        Map<String, Object> result = new HashMap<>();

        // 반납할 대여 내역 조회
        Rental rental = rentalRepository.findByCustomerIdAndRentalEndDateIsNull(customerId)
            .stream()
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("반납할 대여 내역이 없습니다."));

        // 자전거 상태 업데이트 및 반납 처리
        rental.setReturnBranch(returnBranchName);
        rental.setRentalEndDate(LocalDateTime.now());
        rentalRepository.save(rental);

        Bicycle bicycle = bicycleRepository.findById(rental.getBicycleId())
            .orElseThrow(() -> new IllegalArgumentException("자전거를 찾을 수 없습니다."));
        bicycle.setRentalStatus("1");
        bicycle.setLatitude(returnLatitude);
        bicycle.setLongitude(returnLongitude);

        if (isCustomLocation) {
            bicycle.setBicycleStatus("0");
        }

        bicycleRepository.save(bicycle);
        bicycleRepository.flush();

        // 결제 처리 로직
        paymentsService.calculateAndSavePayment(rental.getRentalId(), customerId);

        result.put("rental", rental);
        result.put("code", 200);
        result.put("msg", "반납 및 결제 처리 완료.");
        return result;
    }



}
