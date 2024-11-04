package com.ttallang.user.rental.service;

import com.ttallang.user.commonModel.Branch;
import com.ttallang.user.commonModel.Bicycle;
import com.ttallang.user.commonModel.Payment;
import com.ttallang.user.commonModel.Rental;
import com.ttallang.user.rental.repository.BicycleRepository;
import com.ttallang.user.rental.repository.BranchRepository;
import com.ttallang.user.rental.repository.PaymentRepository;
import com.ttallang.user.rental.repository.RentalRepository;
import java.time.Duration;
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

    public List<Branch> getBranchesBikes() {
        return branchRepository.findAll();
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
            return "You have an unreturned rental. Please return it before renting a new bicycle.";
        }

        Optional<Bicycle> bicycleOptional = bicycleRepository.findById(bicycleId);
        if (bicycleOptional.isPresent()) {
            Bicycle bicycle = bicycleOptional.get();
            if (!"0".equals(bicycle.getRentalStatus())) {
                return "Bicycle is currently rented out";
            }
            bicycle.setRentalStatus("1");
            bicycleRepository.save(bicycle);
        } else {
            return "Bicycle not found";
        }

        Rental rental = new Rental();
        rental.setBicycleId(bicycleId);
        rental.setCustomerId(customerId);
        rental.setRentalBranch(rentalBranch);
        rental.setRentalStartDate(LocalDateTime.now());
        rentalRepository.save(rental);
        return "Rental successful";
    }

    @Transactional
    public String returnBicycle(int customerId, double returnLatitude, double returnLongitude, boolean isCustomLocation) {
        List<Rental> activeRentals = rentalRepository.findByCustomerIdAndRentalEndDateIsNull(customerId);
        if (activeRentals.isEmpty()) {
            return "No active rental found to return.";
        }

        Rental rental = activeRentals.get(0);
        String returnBranchName = isCustomLocation ? "기타" : determineReturnBranch(returnLatitude, returnLongitude);

        rental.setReturnBranch(returnBranchName);
        rental.setRentalEndDate(LocalDateTime.now());

        Optional<Bicycle> bicycleOptional = bicycleRepository.findById(rental.getBicycleId());
        if (bicycleOptional.isPresent()) {
            Bicycle bicycle = bicycleOptional.get();
            bicycle.setRentalStatus("0");
            bicycle.setLatitude(returnLatitude);
            bicycle.setLongitude(returnLongitude);
            bicycleRepository.save(bicycle);
        } else {
            return "Bicycle not found";
        }
        rentalRepository.save(rental);

        calculateAndSavePayment(rental, customerId);
        return "Return successful";
    }

    private String determineReturnBranch(double returnLatitude, double returnLongitude) {
        List<Branch> nearbyBranches = branchRepository.findNearbyBranches(returnLatitude, returnLongitude, 0.00001);
        return nearbyBranches.isEmpty() ? "기타" : nearbyBranches.get(0).getBranchName();
    }

    private void calculateAndSavePayment(Rental rental, int customerId) {
        long minutesRented = Duration.between(rental.getRentalStartDate(), rental.getRentalEndDate()).toMinutes();

        int unlockFee = 500;
        int perMinuteRate = 150;
        int additionalFee = (minutesRented > 0) ? (int) minutesRented * perMinuteRate : 0;

        int totalAmount = unlockFee + additionalFee;

        Payment payment = new Payment();
        payment.setRentalId(rental.getRentalId());
        payment.setCustomerId(customerId);
        payment.setPaymentAmount(totalAmount);
        payment.setPaymentStatus("0"); // 결제 전 상태

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
}