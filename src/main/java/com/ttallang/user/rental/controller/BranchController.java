package com.ttallang.user.rental.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ttallang.user.commonModel.Bicycle;
import com.ttallang.user.commonModel.Branch;
import com.ttallang.user.rental.service.BranchService;

@Controller
@RequestMapping("/map")
public class BranchController {

    private final BranchService branchService;

    @Autowired
    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

//    @GetMapping("/main")
//    public String mainpage() {
//        return "map/mymap";
//    }

    // 대여소 목록을 JSON 형식으로 반환하여 지도에 마커 표시
    @GetMapping("/branches")
    @ResponseBody
    public List<Branch> getBranches() {
        return branchService.getBranchesBikes();
    }

    // 특정 위치의 대여 가능한 자전거 수를 반환
    @GetMapping("/available/bikes/location")
    @ResponseBody
    public int getAvailableBikesAtLocation(@RequestParam("latitude") double latitude,
        @RequestParam("longitude") double longitude) {
        return branchService.getAvailableBikesAtLocation(latitude, longitude);
    }

    // 특정 위치에서 대여 가능한 자전거 목록을 반환
    @GetMapping("/available/bikes")
    @ResponseBody
    public List<Bicycle> getAvailableBikes(@RequestParam("latitude") double latitude,
        @RequestParam("longitude") double longitude) {
        return branchService.getAvailableBikesList(latitude, longitude);
    }

    // 대여 기능 엔드포인트 추가
    @PostMapping("/rent/bicycle")
    @ResponseBody
    public ResponseEntity<String> rentBicycle(
        @RequestParam int bicycleId,
        @RequestParam int customerId,
        @RequestParam String rentalBranch) {

        String result = branchService.rentBicycle(bicycleId, customerId, rentalBranch);
        if (result.equals("Rental successful")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }
}
