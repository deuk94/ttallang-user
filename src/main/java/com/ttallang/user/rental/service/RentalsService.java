package com.ttallang.user.rental.service;

import com.ttallang.user.commonModel.Bicycle;
import com.ttallang.user.commonModel.Payment;
import com.ttallang.user.commonModel.Rental;
import com.ttallang.user.rental.model.UseRental;
import com.ttallang.user.commomRepository.BicycleRepository;
import com.ttallang.user.commomRepository.RentalRepository;
import com.ttallang.user.commomRepository.PaymentRepository;
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
    public Rental rentBicycle(int bicycleId, String rentalBranch, int customerId) {

        // 결제 상태 확인 - 미결제 상태가 있을 경우 예외 발생
        if (!paymentRepository.findByCustomerIdAndPaymentStatus(customerId, "0").isEmpty()) {
            throw new IllegalArgumentException("결제되지 않은 자전거가 있습니다. 결제를 완료해 주세요.");
        }

        // 대여 중인 자전거가 있는지 확인
        if (!rentalRepository.findByCustomerIdAndRentalEndDateIsNull(customerId).isEmpty()) {
            throw new IllegalArgumentException("이미 대여 중인 자전거가 있습니다. 반납 후 새로운 대여가 가능합니다.");
        }

        // 자전거 대여 상태 확인
        Bicycle bicycle = bicycleRepository.findById(bicycleId)
            .orElseThrow(() -> new IllegalArgumentException("자전거를 찾을 수 없습니다."));
        if (!"1".equals(bicycle.getRentalStatus()) || !"1".equals(bicycle.getBicycleStatus())) {
            throw new IllegalStateException("이 자전거는 대여할 수 없는 상태입니다.");
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
        return rentalRepository.save(rental);
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
}
