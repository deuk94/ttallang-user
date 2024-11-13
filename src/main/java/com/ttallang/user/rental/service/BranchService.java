package com.ttallang.user.rental.service;

import com.ttallang.user.commonModel.Branch;
import com.ttallang.user.commomRepository.BranchRepository;
import com.ttallang.user.commonModel.Bicycle;
import com.ttallang.user.commomRepository.BicycleRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BranchService {

    private final BranchRepository branchRepository;
    private final BicycleRepository bicycleRepository;

    @Autowired
    public BranchService(BranchRepository branchRepository, BicycleRepository bicycleRepository) {
        this.branchRepository = branchRepository;
        this.bicycleRepository = bicycleRepository;
    }

    // 활성화된 대여소 목록 가져오기
    public List<Branch> getActiveBranches() {
        return branchRepository.findActiveBranches();
    }

    // 특정 위치에서 사용 가능한 자전거 수 조회
    public int getAvailableBikesAtLocation(double latitude, double longitude) {
        double distance = 0.00001;
        return bicycleRepository.findByBikeCount(latitude, longitude, distance);
    }

    // 특정 위치에서 사용 가능한 자전거 목록 조회
    public List<Bicycle> getAvailableBikesList(double latitude, double longitude) {
        double distance = 0.00001;
        return bicycleRepository.findAvailableBike(latitude, longitude, distance);
    }
    // 특정 위치에서 100미터 내 대여소 이름 조회
    public String getNearbyBranchName(double latitude, double longitude) {
        double distance = 0.0009; // 100미터
        Optional<String> branchName = branchRepository.findNearbyBranchName(latitude, longitude, distance);
        return branchName.orElse("기타");  // 가까운 대여소가 없으면 "기타" 반환
    }
}