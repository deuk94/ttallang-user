package com.ttallang.user.rental.service;

import com.ttallang.user.commonModel.Bicycle;
import com.ttallang.user.commonModel.Rental;
import com.ttallang.user.rental.model.UseRental;
import com.ttallang.user.commomRepository.BicycleRepository;
import com.ttallang.user.commomRepository.RentalRepository;
import com.ttallang.user.commomRepository.PaymentRepository;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public RentalsService(BicycleRepository bicycleRepository, RentalRepository rentalRepository,
        PaymentRepository paymentRepository,
        PaymentsService paymentsService) {
        this.bicycleRepository = bicycleRepository;
        this.rentalRepository = rentalRepository;
        this.paymentRepository = paymentRepository;
        this.paymentsService = paymentsService;
    }

    // 자전거 대여
    public ResponseEntity<?> rentBicycle(int bicycleId, String rentalBranch, int customerId) {
        // 결제 상태 확인 - 미결제 상태가 있을 경우 예외 발생
        if (!paymentRepository.findByCustomerIdAndPaymentStatus(customerId, "0").isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NoPay");
        }

        // 대여 중인 자전거가 있는지 확인
        if (rentalRepository.findByCustomerIdAndRentalEndDateIsNull(customerId) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("renting");
        }
        // 자전거 대여 조회
        Bicycle bicycle = bicycleRepository.findById(bicycleId)
            .orElseThrow();

        // 대여중 상태 변경
        bicycle.setRentalStatus("0");
        bicycleRepository.save(bicycle);

        // 대여 내역 저장
        Rental rental = new Rental();
        rental.setBicycleId(bicycleId);
        rental.setCustomerId(customerId);
        rental.setRentalBranch(rentalBranch);
        rental.setRentalStartDate(LocalDateTime.now());
        Rental saveRental = rentalRepository.save(rental);
        return ResponseEntity.ok(saveRental);
    }

    // 자전거 반납
    public Rental returnBicycle(double returnLatitude, double returnLongitude,
        String returnBranchName, int customerId) {

        // 반납할 대여 내역 조회
        Rental rental = rentalRepository.findByCustomerIdAndRentalEndDateIsNull(customerId);

        // 자전거 상태 업데이트 및 반납 처리
        rental.setReturnBranch(returnBranchName);
        rental.setRentalEndDate(LocalDateTime.now());
        rentalRepository.save(rental);

        Bicycle bicycle = bicycleRepository.findById(rental.getBicycleId())
            .orElseThrow(() -> new IllegalArgumentException("자전거를 찾을 수 없습니다."));
        bicycle.setRentalStatus("1");
        bicycle.setLatitude(returnLatitude);
        bicycle.setLongitude(returnLongitude);

        if (returnBranchName.equals("기타")) {
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
    public Optional<Integer> getCurrentRentalByCustomerId(int customerId) {

        Rental rentals = rentalRepository.findByCustomerIdAndRentalEndDateIsNull(customerId);
        if (rentals != null) {
            return Optional.of(rentals.getBicycleId());
        }
        return Optional.empty();
    }

    // 현황판을 위한 메서드
    public ResponseEntity<?> getRentalStatusForCustomer(int customerId) {
        Map<String, Object> rentalStatus = new HashMap<>();
        Rental currentRental = rentalRepository.findByCustomerIdAndRentalEndDateIsNull(customerId);
        if (currentRental != null) {
            Optional<Bicycle> rentedBicycle = bicycleRepository.findById(
                currentRental.getBicycleId());
            if (rentedBicycle.isPresent()) {
                rentalStatus.put("bicycleId", rentedBicycle.get().getBicycleId());
                rentalStatus.put("bicycleName", rentedBicycle.get().getBicycleName());
                rentalStatus.put("rentalBranch", currentRental.getRentalBranch());
                rentalStatus.put("rentalStartDate", currentRental.getRentalStartDate());
                rentalStatus.put("currentLatitude", rentedBicycle.get().getLatitude());
                rentalStatus.put("currentLongitude", rentedBicycle.get().getLongitude());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NoBicycle");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NoRental");
        }
        return ResponseEntity.ok(rentalStatus);
    }
}