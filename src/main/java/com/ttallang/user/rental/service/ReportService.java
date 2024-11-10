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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    private final FaultReportRepository faultReportRepository;
    private final BicycleRepository bicycleRepository;
    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final FaultCategoryRepository faultCategoryRepository;

    @Autowired
    public ReportService(FaultReportRepository faultReportRepository,
        BicycleRepository bicycleRepository,
        PaymentRepository paymentRepository,
        RentalRepository rentalRepository,
        FaultCategoryRepository faultCategoryRepository) {
        this.faultReportRepository = faultReportRepository;
        this.bicycleRepository = bicycleRepository;
        this.paymentRepository = paymentRepository;
        this.rentalRepository = rentalRepository;
        this.faultCategoryRepository = faultCategoryRepository;
    }

    // 미처리 신고 여부 확인 메서드
    public boolean hasUnresolvedReport(int customerId) {
        return faultReportRepository.existsByCustomerIdAndReportStatus(customerId, "0");
    }

    // 모든 신고 카테고리 조회
    public List<FaultCategory> getAllFaultCategories() {
        return faultCategoryRepository.findAll();
    }

    // 신고 처리
    public FaultReport reportIssue(int customerId, int bicycleId, int categoryId, String reportDetails) {
        // 미결제 및 미처리 신고 확인
        if (hasUnresolvedReport(customerId)) {
            throw new IllegalArgumentException("처리되지 않은 신고가 있습니다. 기존 신고가 처리된 후 새로운 신고가 가능합니다.");
        }
        if (!paymentRepository.findByCustomerIdAndPaymentStatus(customerId, "0").isEmpty()) {
            throw new IllegalArgumentException("결제되지 않은 자전거가 있습니다.");
        }

        // 카테고리 유효성 확인
        if (!faultCategoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("잘못된 카테고리 ID입니다.");
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
            .orElseThrow(() -> new IllegalArgumentException("자전거를 찾을 수 없습니다."));
        bicycle.setReportStatus("0");
        bicycle.setBicycleStatus("0");
        bicycleRepository.saveAndFlush(bicycle);

        return faultReport;
    }

    // 신고 및 반납 처리 메서드
    // 신고 및 반납 처리 메서드
    public FaultReport reportAndReturn(int customerId, int bicycleId, int categoryId,
        String reportDetails, String returnBranchName, double returnLatitude, double returnLongitude) { // 반납 위치 정보 추가

        // 미결제 및 미처리 신고 확인
        if (hasUnresolvedReport(customerId)) {
            throw new IllegalArgumentException("처리되지 않은 신고가 있습니다. 기존 신고가 처리된 후 새로운 신고가 가능합니다.");
        }
        if (!paymentRepository.findByCustomerIdAndPaymentStatus(customerId, "0").isEmpty()) {
            throw new IllegalArgumentException("결제되지 않은 자전거가 있습니다.");
        }

        // 카테고리 유효성 확인
        if (!faultCategoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("잘못된 카테고리 ID입니다.");
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
            .orElseThrow(() -> new IllegalArgumentException("자전거를 찾을 수 없습니다."));
        bicycle.setReportStatus("0");
        bicycle.setBicycleStatus("0");
        bicycle.setRentalStatus("1");
        bicycle.setLatitude(returnLatitude);
        bicycle.setLongitude(returnLongitude);
        System.out.println("Latitude: " + bicycle.getLatitude());
        System.out.println("Longitude: " + bicycle.getLongitude());
        bicycleRepository.save(bicycle);
        bicycleRepository.flush();

        // 대여 상태 업데이트: 활성 대여 정보 반납 시간 및 반납 지점 설정
        Optional<Rental> activeRental = rentalRepository.findActiveRental(customerId);
        if (activeRental.isPresent()) {
            Rental rental = activeRental.get();
            rental.setRentalEndDate(LocalDateTime.now());
            rental.setReturnBranch(returnBranchName);
            rentalRepository.save(rental);
        } else {
            throw new IllegalArgumentException("현재 대여 중인 자전거가 없습니다.");
        }

        return faultReport;
    }

}
