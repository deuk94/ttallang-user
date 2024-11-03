package com.ttallang.user.rental.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ttallang.user.commonModel.Bicycle;
import com.ttallang.user.commonModel.Branch;
import com.ttallang.user.rental.service.BranchService;

@RestController
@RequestMapping("/api/map") // REST API 경로를 명확히 하기 위해 prefix 추가
public class RestBranchController {

    private final BranchService branchService;

    @Autowired
    public RestBranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    // 모든 대여소 정보를 반환 (JSON 형식)
    @GetMapping("/branches")
    public List<Branch> getBranches() {
        return branchService.getBranchesBikes();
    }

    // 특정 위치의 대여 가능한 자전거 수를 반환
    @GetMapping("/available-bikes-at-location")
    public Map<String, Integer> getAvailableBikesAtLocation(@RequestParam("latitude") double latitude,
        @RequestParam("longitude") double longitude) {
        Map<String, Integer> response = new HashMap<>();
        response.put("available bikes", branchService.getAvailableBikesAtLocation(latitude, longitude));
        return response;
    }

    // 특정 위치에서 대여 가능한 자전거 목록을 반환
    @GetMapping("/available-bikes")
    public List<Bicycle> getAvailableBikes(@RequestParam("latitude") double latitude,
        @RequestParam("longitude") double longitude) {
        return branchService.getAvailableBikesList(latitude, longitude);
    }
}
