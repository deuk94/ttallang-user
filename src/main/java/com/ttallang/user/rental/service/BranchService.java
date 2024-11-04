package com.ttallang.user.rental.service;

import com.ttallang.user.commonModel.Branch;
import com.ttallang.user.commonModel.Rental;
import com.ttallang.user.rental.repository.BicycleRepository;
import com.ttallang.user.rental.repository.BranchRepository;
import com.ttallang.user.rental.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ttallang.user.commonModel.Bicycle;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BranchService {

    private final BranchRepository branchRepository;
    private final BicycleRepository bicycleRepository;
    private final RentalRepository rentalRepository;

    @Autowired
    public BranchService(BranchRepository branchRepository, BicycleRepository bicycleRepository, RentalRepository rentalRepository) {
        this.branchRepository = branchRepository;
        this.bicycleRepository = bicycleRepository;
        this.rentalRepository = rentalRepository;
    }

    // 대여소 목록을 반환
    public List<Branch> getBranchesBikes() {
        return branchRepository.findAll();
    }

    // 특정 위치에서 대여 가능한 자전거 수를 조회
    public int getAvailableBikesAtLocation(double latitude, double longitude) {
        double distance = 0.00001; // 10m 거리안에 자전거가 존재하면 대여소에 포함.
        return bicycleRepository.findByBikeCount(latitude, longitude, distance);
    }

    // 특정 위치에서 대여 가능한 자전거 목록 조회
    public List<Bicycle> getAvailableBikesList(double latitude, double longitude) {
        double distance = 0.00001;
        return bicycleRepository.findAvailableBike(latitude, longitude, distance);
    }

    // 대여 기능 메서드 추가
    public String rentBicycle(int bicycleId, int customerId, String rentalBranch) {
        // 1. 자전거 상태 업데이트 (대여중으로 변경)
        Optional<Bicycle> bicycleOptional = bicycleRepository.findById(bicycleId);
        if (bicycleOptional.isPresent()) {
            Bicycle bicycle = bicycleOptional.get();
            bicycle.setRentalStatus("1"); // 1: 렌탈 완료
            bicycleRepository.save(bicycle);
        } else {
            return "Bicycle not found";
        }

        // 2. 렌탈 정보 저장
        Rental rental = new Rental();
        rental.setBicycleId(bicycleId);
        rental.setCustomerId(customerId);
        rental.setRentalBranch(rentalBranch);
        rental.setRentalStartDate(LocalDateTime.now());

        rentalRepository.save(rental);

        return "Rental successful";
    }
}
