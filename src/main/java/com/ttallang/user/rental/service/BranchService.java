package com.ttallang.user.rental.service;

import com.ttallang.user.commomRepository.PaymentRepository;
import com.ttallang.user.commomRepository.RentalRepository;
import com.ttallang.user.commonModel.Branch;
import com.ttallang.user.commonModel.Bicycle;
import com.ttallang.user.commonModel.FaultReport;
import com.ttallang.user.commonModel.Payment;
import com.ttallang.user.commonModel.Rental;
import com.ttallang.user.commomRepository.BicycleRepository;
import com.ttallang.user.commomRepository.BranchRepository;
import com.ttallang.user.commomRepository.FaultReportRepository;
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
    private final FaultReportRepository faultReportRepository;

    @Autowired
    public BranchService(BranchRepository branchRepository, BicycleRepository bicycleRepository,
        RentalRepository rentalRepository, PaymentRepository paymentRepository,
        FaultReportRepository faultReportRepository) {
        this.branchRepository = branchRepository;
        this.bicycleRepository = bicycleRepository;
        this.rentalRepository = rentalRepository;
        this.paymentRepository = paymentRepository;
        this.faultReportRepository = faultReportRepository;
    }

    public List<Branch> getActiveBranches() {
        return branchRepository.findActiveBranches();
    }

    public int getAvailableBikesAtLocation(double latitude, double longitude) {
        double distance = 0.00001;
        return bicycleRepository.findByBikeCount(latitude, longitude, distance);
    }

    public List<Bicycle> getAvailableBikesList(double latitude, double longitude) {
        double distance = 0.00001;
        return bicycleRepository.findAvailableBike(latitude, longitude, distance);
    }

    public String rentBicycle(int bicycleId, int customerId, String rentalBranch) {
        List<Rental> activeRentals = rentalRepository.findByCustomerIdAndRentalEndDateIsNull(customerId);
        if (!activeRentals.isEmpty()) {
            return "반납되지 않은 대여가 있습니다. 새 자전거를 대여하기 전에 반납해 주세요.";
        }

        Optional<Bicycle> bicycleOptional = bicycleRepository.findById(bicycleId);
        if (bicycleOptional.isPresent()) {
            Bicycle bicycle = bicycleOptional.get();
            if (!"1".equals(bicycle.getRentalStatus())) {
                return "이 자전거는 이미 대여 중입니다.";
            }
            bicycle.setRentalStatus("0");
            bicycleRepository.save(bicycle);
        } else {
            return "자전거를 찾을 수 없습니다.";
        }

        Rental rental = new Rental();
        rental.setBicycleId(bicycleId);
        rental.setCustomerId(customerId);
        rental.setRentalBranch(rentalBranch);
        rental.setRentalStartDate(LocalDateTime.now());
        rentalRepository.save(rental);
        return "대여가 성공적으로 완료되었습니다.";
    }

    @Transactional
    public String returnBicycle(int customerId, double returnLatitude, double returnLongitude, boolean isCustomLocation) {
        List<Rental> activeRentals = rentalRepository.findByCustomerIdAndRentalEndDateIsNull(customerId);
        if (activeRentals.isEmpty()) {
            return "반납할 대여 내역이 없습니다.";
        }

        Rental rental = activeRentals.get(0);
        String returnBranchName = isCustomLocation ? "기타" : determineReturnBranch(returnLatitude, returnLongitude);

        rental.setReturnBranch(returnBranchName);
        rental.setRentalEndDate(LocalDateTime.now());

        Optional<Bicycle> bicycleOptional = bicycleRepository.findById(rental.getBicycleId());
        if (bicycleOptional.isPresent()) {
            Bicycle bicycle = bicycleOptional.get();
            bicycle.setRentalStatus("1"); // 반납 상태로 변경
            bicycle.setLatitude(returnLatitude);
            bicycle.setLongitude(returnLongitude);
            bicycleRepository.save(bicycle);
        } else {
            return "자전거를 찾을 수 없습니다.";
        }
        rentalRepository.save(rental);

        calculateAndSavePayment(rental, customerId);

        return "반납이 성공적으로 완료되었습니다.";
    }

    private String determineReturnBranch(double returnLatitude, double returnLongitude) {
        List<Branch> nearbyBranches = branchRepository.findNearbyBranches(returnLatitude, returnLongitude, 0.00001);
        return nearbyBranches.isEmpty() ? "기타" : nearbyBranches.get(0).getBranchName();
    }

    private void calculateAndSavePayment(Rental rental, int customerId) {
        Payment payment = new Payment();
        payment.setRentalId(rental.getRentalId());
        payment.setCustomerId(customerId);
        payment.setPaymentAmount(null);
        payment.setPaymentStatus("0");

        paymentRepository.save(payment);
    }

    public Optional<Bicycle> getCurrentRentalByCustomerId(int customerId) {
        List<Rental> rentals = rentalRepository.findByCustomerIdAndRentalEndDateIsNull(customerId);
        if (!rentals.isEmpty()) {
            int bicycleId = rentals.get(0).getBicycleId();
            return bicycleRepository.findById(bicycleId);
        }
        return Optional.empty();
    }

    @Transactional
    public String reportIssue(int customerId, int bicycleId, int categoryId, String reportDetails) {
        // categoryId 검증
        if (categoryId != 1 && categoryId != 2) {
            return "잘못된 카테고리 ID입니다.";
        }

        // 신고 테이블에 새로운 신고 저장
        FaultReport faultReport = new FaultReport();
        faultReport.setCustomerId(customerId);
        faultReport.setBicycleId(bicycleId);
        faultReport.setCategoryId(categoryId);
        faultReport.setReportDate(LocalDateTime.now());
        faultReport.setReportDetails(reportDetails);
        faultReport.setReportStatus("0"); // 신고 상태를 0으로 설정
        faultReport.setFaultStatus("1"); // 고장 상태로 설정
        faultReportRepository.save(faultReport);

        // 자전거 테이블에서 해당 자전거의 reportStatus를 0으로 업데이트
        Optional<Bicycle> bicycleOptional = bicycleRepository.findById(bicycleId);
        if (bicycleOptional.isPresent()) {
            Bicycle bicycle = bicycleOptional.get();
            bicycle.setReportStatus("0"); // 자전거의 신고 상태를 0으로 설정
            bicycleRepository.saveAndFlush(bicycle); // 명시적으로 저장 후 즉시 DB 반영
        } else {
            return "자전거를 찾을 수 없습니다.";
        }

        return "신고가 접수되었습니다.";
    }
}
