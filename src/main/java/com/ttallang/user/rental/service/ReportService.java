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
    private final PaymentsService paymentsService;

    @Autowired
    public ReportService(FaultReportRepository faultReportRepository,
        BicycleRepository bicycleRepository,
        PaymentRepository paymentRepository,
        RentalRepository rentalRepository,
        FaultCategoryRepository faultCategoryRepository, PaymentsService paymentsService) {
        this.faultReportRepository = faultReportRepository;
        this.bicycleRepository = bicycleRepository;
        this.paymentRepository = paymentRepository;
        this.rentalRepository = rentalRepository;
        this.faultCategoryRepository = faultCategoryRepository;
        this.paymentsService = paymentsService;
    }

    // 모든 신고 카테고리 조회
    public List<FaultCategory> getAllFaultCategories() {
        return faultCategoryRepository.findAll();
    }

    // 신고 처리
    public Map<String, Object> reportIssue(int customerId, int bicycleId, int categoryId, String reportDetails) {
        Map<String, Object> result = new HashMap<>();

        // 미결제 상태 확인
        if (!paymentRepository.findByCustomerIdAndPaymentStatus(customerId, "0").isEmpty()) {
            result.put("report", null);
            result.put("code", 403);
            result.put("msg", "결제되지 않은 자전거가 있습니다. 결제 후 신고가 가능합니다.");
            return result;
        }

        // 유효한 카테고리인지 확인
        if (!faultCategoryRepository.existsById(categoryId)) {
            result.put("report", null);
            result.put("code", 400);
            result.put("msg", "잘못된 카테고리 ID입니다.");
            return result;
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
            .orElseThrow(() -> {
                result.put("report", null);
                result.put("code", 404);
                result.put("msg", "자전거를 찾을 수 없습니다.");
                return null;
            });

        bicycle.setReportStatus("0");
        bicycle.setBicycleStatus("0");
        bicycleRepository.saveAndFlush(bicycle);

        result.put("report", faultReport);
        result.put("code", 200);
        result.put("msg", "신고가 성공적으로 접수되었습니다.");
        return result;
    }

    // 신고 및 반납 처리 메서드
    public Map<String, Object> reportAndReturn(int customerId, int bicycleId, int categoryId,
        String reportDetails, String returnBranchName, double returnLatitude, double returnLongitude) {

        Map<String, Object> result = new HashMap<>();

        // 유효한 카테고리인지 확인
        if (!faultCategoryRepository.existsById(categoryId)) {
            result.put("report", null);
            result.put("code", 400);
            result.put("msg", "잘못된 카테고리 ID입니다.");
            return result;
        }

        // 자전거 정보 확인
        Bicycle bicycle = bicycleRepository.findById(bicycleId)
            .orElseThrow(() -> {
                result.put("report", null);
                result.put("code", 404);
                result.put("msg", "자전거를 찾을 수 없습니다.");
                return null;
            });

        // 대여 상태 확인
        Optional<Rental> activeRental = rentalRepository.findActiveRental(customerId);
        if (!activeRental.isPresent()) {
            result.put("report", null);
            result.put("code", 404);
            result.put("msg", "현재 대여 중인 자전거가 없습니다.");
            return result;
        }

        Rental rental = activeRental.get();
//        Duration rentalDuration = Duration.between(rental.getRentalStartDate(), LocalDateTime.now());
//
//        boolean shouldRedirectToPayment = rentalDuration.toMinutes() > 5;

        // 테스트용 30초로 제한둠
        Duration rentalDuration = Duration.between(rental.getRentalStartDate(), LocalDateTime.now());
        boolean shouldRedirectToPayment = rentalDuration.toSeconds() > 30;

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
        bicycle.setReportStatus("0");
        bicycle.setBicycleStatus("0");
        bicycle.setRentalStatus("1");
        bicycle.setLatitude(returnLatitude);
        bicycle.setLongitude(returnLongitude);
        bicycleRepository.save(bicycle);
        bicycleRepository.flush();

        // 대여 종료 업데이트
        rental.setRentalEndDate(LocalDateTime.now());
        rental.setReturnBranch(returnBranchName);
        rentalRepository.save(rental);

        // 반납 시간에 따른 메시지 설정
        if (shouldRedirectToPayment) {
            result.put("msg", "신고 및 자전거 반납이 완료되었습니다. 이용 시간에 따른 결제가 필요합니다. 결제 페이지로 이동합니다.");
            paymentsService.calculateAndSavePayment(rental.getRentalId(), customerId);
        } else {
            result.put("msg", "신고 및 자전거 반납이 성공적으로 완료되었습니다. 추가 결제는 필요하지 않습니다.");
        }

        result.put("report", faultReport);
        result.put("code", 200);
//        result.put("msg", "신고 및 자전거 반납이 성공적으로 완료되었습니다.");
        result.put("redirectToPayment", shouldRedirectToPayment);


        return result;
    }

}