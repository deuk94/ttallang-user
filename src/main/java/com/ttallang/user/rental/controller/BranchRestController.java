package com.ttallang.user.rental.controller;

import com.ttallang.user.commonModel.Bicycle;
import com.ttallang.user.commonModel.Branch;
import com.ttallang.user.commonModel.FaultReport;
import com.ttallang.user.commonModel.FaultCategory;
import com.ttallang.user.commonModel.Rental;
import com.ttallang.user.rental.model.UseRental;
import com.ttallang.user.rental.service.BranchService;
import com.ttallang.user.rental.service.RentalsService;
import com.ttallang.user.rental.service.ReportService;
import com.ttallang.user.security.config.auth.PrincipalDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/map")
public class BranchRestController {

    private final BranchService branchService;
    private final RentalsService rentalsService;
    private final ReportService reportService;

    @Autowired
    public BranchRestController(BranchService branchService, RentalsService rentalsService,
        ReportService reportService) {
        this.branchService = branchService;
        this.rentalsService = rentalsService;
        this.reportService = reportService;
    }

    @GetMapping("/branches")
    public List<Branch> getBranches() {
        return branchService.getActiveBranches();
    }

    @GetMapping("/available/bikes/location")
    public Integer getAvailableBikesAtLocation(@RequestParam("latitude") double latitude,
        @RequestParam("longitude") double longitude) {
        return branchService.getAvailableBikesAtLocation(latitude, longitude);
    }

    @GetMapping("/available/bikes")
    public List<Bicycle> getAvailableBikes(@RequestParam("latitude") double latitude,
        @RequestParam("longitude") double longitude) {
        return branchService.getAvailableBikesList(latitude, longitude);
    }

    @PostMapping("/rent/bicycle")
    public Rental rentBicycle(@RequestParam int bicycleId, @RequestParam String rentalBranch) {
        PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loginId = pds.getCustomerID();
        return rentalsService.rentBicycle(bicycleId, rentalBranch, loginId);
    }

    @PostMapping("/return/bicycle")
    public Rental returnBicycle(@RequestParam double returnLatitude, @RequestParam double returnLongitude,
        @RequestParam boolean isCustomLocation, @RequestParam String returnBranchName) {
        PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loginId = pds.getCustomerID();
        return rentalsService.returnBicycle(returnLatitude, returnLongitude, isCustomLocation, returnBranchName, loginId);
    }

    @GetMapping("/check-rental-status")
    public Bicycle checkRentalStatus() {
        PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loginId = pds.getCustomerID();
        return rentalsService.getCurrentRentalByCustomerId(loginId).orElse(null);
    }

    @GetMapping("/current-rentals")
    public UseRental getCurrentRentals() {
        PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loginId = pds.getCustomerID();
        return rentalsService.getCurrentRentalsByCustomerId(loginId);
    }

    @GetMapping("/report-categories")
    public List<FaultCategory> getReportCategories() {
        return reportService.getAllFaultCategories();
    }

    @PostMapping("/report-issue")
    public FaultReport reportIssue(@RequestParam int bicycleId, @RequestParam int categoryId,
        @RequestParam String reportDetails) {
        PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loginId = pds.getCustomerID();
        return reportService.reportIssue(loginId, bicycleId, categoryId, reportDetails);
    }

    @PostMapping("/report-and-return")
    public FaultReport reportAndReturn(
        @RequestParam int bicycleId,
        @RequestParam int categoryId,
        @RequestParam String reportDetails,
        @RequestParam String returnBranchName,
        @RequestParam double returnLatitude,
        @RequestParam double returnLongitude) {

        System.out.println("Controller - Latitude: " + returnLatitude);
        System.out.println("Controller - Longitude: " + returnLongitude);

        PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loginId = pds.getCustomerID();

        return reportService.reportAndReturn(loginId, bicycleId, categoryId, reportDetails, returnBranchName, returnLatitude, returnLongitude);
    }


}

