package com.ttallang.user.rental.controller;

import com.ttallang.user.commonModel.Bicycle;
import com.ttallang.user.commonModel.Branch;
import com.ttallang.user.commonModel.FaultCategory;
import com.ttallang.user.commonModel.Rental;
import com.ttallang.user.rental.model.UseRental;
import com.ttallang.user.rental.service.BranchService;
import com.ttallang.user.rental.service.RentalsService;
import com.ttallang.user.rental.service.ReportService;
import com.ttallang.user.security.config.auth.PrincipalDetails;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/map")
public class RentalRestController {

    private final BranchService branchService;
    private final RentalsService rentalsService;
    private final ReportService reportService;

    @Autowired
    public RentalRestController(BranchService branchService, RentalsService rentalsService,
        ReportService reportService) {
        this.branchService = branchService;
        this.rentalsService = rentalsService;
        this.reportService = reportService;
    }
    //활성화된 대여소 정보 가져오기
    @GetMapping("/branches")
    public List<Branch> getBranches() {
        return branchService.getActiveBranches();
    }
    //대여소 안에 속한 자전거 정보 가져오기
    @PostMapping("/available/bicycle")
    public List<Bicycle> getAvailableBikes(@RequestBody Map<String, Double> location) {
        double latitude = location.get("latitude");
        double longitude = location.get("longitude");
        return branchService.getAvailableBikesList(latitude, longitude);
    }
    //현황판
    @GetMapping("/rental/status")
    public ResponseEntity<?> getRentalStatus() {
        PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loginId = pds.getCustomerID();
        return rentalsService.getRentalStatusForCustomer(loginId);
    }
    //자전 대여
    @PostMapping("/rent/bicycle")
    public ResponseEntity<?> rentBicycle(@RequestBody Map<String, String> rentBicycle) {
        PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loginId = pds.getCustomerID();
        int bicycleId = Integer.parseInt(rentBicycle.get("bicycleId"));
        String rentalBranch =  rentBicycle.get("rentalBranch");
        return rentalsService.rentBicycle(bicycleId, rentalBranch, loginId);
    }

    //근처 대여소 찾기
    @GetMapping("/nearBranch")
    public String getNearbyBranch(@RequestParam double latitude, @RequestParam double longitude) {
        return branchService.getNearbyBranchName(latitude, longitude);
    }

    @PostMapping("/return/bicycle")
    public Rental returnBicycle(@RequestParam double returnLatitude, @RequestParam double returnLongitude,
        @RequestParam String returnBranchName) {
        PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loginId = pds.getCustomerID();
        return rentalsService.returnBicycle(returnLatitude, returnLongitude, returnBranchName, loginId);
    }

    // 대여 상태 조회
    @GetMapping("/checkRentalStatus")
    public Integer checkRentalStatus() {
        PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loginId = pds.getCustomerID();
        Integer integer = rentalsService.getCurrentRentalByCustomerId(loginId).orElse(-1);
        System.out.println("+++++++++" + integer);
        return integer;
    }
    //대여 정보 조회
    @GetMapping("/currentRental")
    public UseRental getCurrentRentals() {
        PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loginId = pds.getCustomerID();
        return rentalsService.getCurrentRentalsByCustomerId(loginId);
    }
    //신고 카테고리 조회
    @GetMapping("/reportCategories")
    public List<FaultCategory> getReportCategories() {
        return reportService.getAllFaultCategories();
    }
    //신고하기
    @PostMapping("/report")
    public ResponseEntity<?> reportIssue(@RequestParam int bicycleId, @RequestParam int categoryId,
        @RequestParam String reportDetails, @RequestParam double latitude,
        @RequestParam double longitude) {
        PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loginId = pds.getCustomerID();
        System.out.println("returnLatitude : " + latitude + "returnLongitude" + longitude);
        return reportService.reportIssue(loginId, bicycleId, categoryId, reportDetails, latitude, longitude);
    }

}