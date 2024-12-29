package com.ttallang.user.rental.service;

import com.ttallang.user.commomRepository.BicycleRepository;
import com.ttallang.user.commomRepository.FaultReportRepository;
import com.ttallang.user.commomRepository.PaymentRepository;
import com.ttallang.user.commomRepository.RentalRepository;
import com.ttallang.user.commomRepository.FaultCategoryRepository;
import com.ttallang.user.commonModel.Bicycle;
import com.ttallang.user.commonModel.FaultCategory;
import com.ttallang.user.commonModel.FaultReport;
import com.ttallang.user.commonModel.Payment;
import com.ttallang.user.commonModel.Rental;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final FaultReportRepository faultReportRepository;
    private final BicycleRepository bicycleRepository;
    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final FaultCategoryRepository faultCategoryRepository;
    private final PaymentsService paymentsService;
    private final BranchService branchService;

    @Autowired
    public ReportService(FaultReportRepository faultReportRepository,
        BicycleRepository bicycleRepository,
        PaymentRepository paymentRepository,
        RentalRepository rentalRepository,
        FaultCategoryRepository faultCategoryRepository, PaymentsService paymentsService,
        BranchService branchService) {
        this.faultReportRepository = faultReportRepository;
        this.bicycleRepository = bicycleRepository;
        this.paymentRepository = paymentRepository;
        this.rentalRepository = rentalRepository;
        this.faultCategoryRepository = faultCategoryRepository;
        this.paymentsService = paymentsService;
        this.branchService = branchService;
    }

    // 모든 신고 카테고리 조회
    public List<FaultCategory> getAllFaultCategories() {
        return faultCategoryRepository.findAll();
    }

    @Transactional
    // 신고 처리
    public ResponseEntity<?> reportIssue(int customerId, int bicycleId, int categoryId, String reportDetails, double latitude, double longitude) {
        // 미결제 상태 확인
        if (!paymentRepository.findByCustomerIdAndPaymentStatus(customerId, "0").isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NoPay");
        }
        // 유효한 카테고리인지 확인
        if (!faultCategoryRepository.existsById(categoryId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NoCategory");
        }

        // 신고 생성 및 저장
        FaultReport faultReport = new FaultReport();
        faultReport.setCustomerId(customerId);
        faultReport.setBicycleId(bicycleId);
        faultReport.setCategoryId(categoryId);
        faultReport.setReportDate(LocalDateTime.now());
        faultReport.setReportDetails(reportDetails);
        faultReport.setReportStatus("0");
        faultReport.setFaultStatus("1");
        faultReportRepository.save(faultReport);

        // 자전거 상태 업데이트
        Bicycle bicycle = bicycleRepository.findById(bicycleId)
            .orElseThrow();
        bicycle.setReportStatus("0");
        bicycle.setBicycleStatus("0");
        bicycle.setRentalStatus("1");
        bicycle.setLatitude(latitude);
        bicycle.setLongitude(longitude);
        Bicycle bicycle1 = bicycleRepository.save(bicycle);
        //만약 대여 중 신고이면
        Optional<Rental> activeRental = rentalRepository.findActiveRental(customerId);
        if(activeRental.isPresent()){
            Rental rental = activeRental.get();
            // 5분안에 결제 확인.
//        Duration rentalDuration = Duration.between(rental.getRentalStartDate(), LocalDateTime.now());
//        boolean shouldRedirectToPayment = rentalDuration.toMinutes() > 5;

            // 테스트용 30초로 제한둠
            Duration rentalDuration = Duration.between(rental.getRentalStartDate(), LocalDateTime.now());
            boolean shouldRedirectToPayment = rentalDuration.toSeconds() > 30;
            String returnBranchName = branchService.getNearbyBranchName(latitude, longitude);
            // 대여 종료 업데이트
            rental.setRentalEndDate(LocalDateTime.now());
            rental.setReturnBranch(returnBranchName);
            rentalRepository.save(rental);
            if (shouldRedirectToPayment) {
                paymentsService.calculateAndSavePayment(rental.getRentalId(), customerId);
                return ResponseEntity.status(HttpStatus.OK).body("After5");
            } else {
                System.out.println("Before5");
                return ResponseEntity.status(HttpStatus.OK).body("Before5");
            }
        }
        return ResponseEntity.ok(bicycle1);
    }
}