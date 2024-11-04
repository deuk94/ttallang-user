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

    @GetMapping("/branches")
    @ResponseBody
    public List<Branch> getBranches() {
        return branchService.getBranchesBikes();
    }

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
        if ("Rental successful".equals(result)) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    @PostMapping("/return/bicycle")
    @ResponseBody
    public ResponseEntity<String> returnBicycle(@RequestParam int customerId,
        @RequestParam double returnLatitude,
        @RequestParam double returnLongitude,
        @RequestParam boolean isCustomLocation) {
        String result = branchService.returnBicycle(customerId, returnLatitude, returnLongitude, isCustomLocation);
        if ("Return successful".equals(result)) {
            return ResponseEntity.ok("Return successful");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    @GetMapping("/check-rental-status")
    @ResponseBody
    public ResponseEntity<Bicycle> checkRentalStatus(@RequestParam("customerId") int customerId) {
        Optional<Bicycle> bicycle = branchService.getCurrentRentalByCustomerId(customerId);
        if (bicycle.isPresent() && "1".equals(bicycle.get().getRentalStatus())) { // Only return if rented
            return new ResponseEntity<>(bicycle.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}