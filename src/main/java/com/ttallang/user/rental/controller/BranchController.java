package com.ttallang.user.rental.controller;

import com.ttallang.user.commonModel.Bicycle;
import com.ttallang.user.commonModel.Branch;
import com.ttallang.user.rental.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/map")
public class BranchController {

    private final BranchService branchService;

    @Autowired
    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    // branch_status가 1인 대여소들만 확인
    @GetMapping("/branches")
    @ResponseBody
    public List<Branch> getBranches() {
        return branchService.getActiveBranches();
    }

    // 팝업창 대여소의 자전거가 몇대 있는지 위치(위도와 경도)
    @GetMapping("/available/bikes/location")
    @ResponseBody
    public int getAvailableBikesAtLocation(@RequestParam("latitude") double latitude,
        @RequestParam("longitude") double longitude) {
        return branchService.getAvailableBikesAtLocation(latitude, longitude);
    }

    @GetMapping("/available/bikes")
    @ResponseBody
    public List<Bicycle> getAvailableBikes(@RequestParam("latitude") double latitude,
        @RequestParam("longitude") double longitude) {
        return branchService.getAvailableBikesList(latitude, longitude);
    }

    @PostMapping("/rent/bicycle")
    @ResponseBody
    public ResponseEntity<String> rentBicycle(@RequestParam int bicycleId,
        @RequestParam int customerId,
        @RequestParam String rentalBranch) {
        String result = branchService.rentBicycle(bicycleId, customerId, rentalBranch);
        if ("대여가 성공적으로 완료되었습니다.".equals(result)) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    @PostMapping("/return/bicycle")
    @ResponseBody
    public ResponseEntity<String> returnBicycle(@RequestParam int customerId,
        @RequestParam double returnLatitude,
        @RequestParam double returnLongitude,
        @RequestParam boolean isCustomLocation) {
        String result = branchService.returnBicycle(customerId, returnLatitude, returnLongitude, isCustomLocation);
        if ("반납이 성공적으로 완료되었습니다.".equals(result)) {
            return ResponseEntity.ok(result);  // 성공 시 200 OK
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);  // 실패 시 400 Bad Request
        }
    }

    @GetMapping("/check-rental-status")
    @ResponseBody
    public ResponseEntity<Bicycle> checkRentalStatus(@RequestParam("customerId") int customerId) {
        Optional<Bicycle> bicycle = branchService.getCurrentRentalByCustomerId(customerId);
        if (bicycle.isPresent() && "0".equals(bicycle.get().getRentalStatus())) {
            return new ResponseEntity<>(bicycle.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    // 위치 신고 및 고장 신고 처리
    @PostMapping("/report-issue")
    @ResponseBody
    public ResponseEntity<String> reportIssue(
        @RequestParam int customerId,
        @RequestParam int bicycleId,
        @RequestParam int categoryId,  // categoryId가 필요합니다.
        @RequestParam String reportDetails) {

        String result = branchService.reportIssue(customerId, bicycleId, categoryId, reportDetails);
        return ResponseEntity.ok(result);
    }
}
