package com.ttallang.user.mypage.controller;

import com.ttallang.user.commonModel.FaultReport;
import com.ttallang.user.commonModel.Roles;
import com.ttallang.user.commonModel.User;
import com.ttallang.user.mypage.model.JoinBicycle;
import com.ttallang.user.mypage.model.JoinFault;
import com.ttallang.user.mypage.model.JoinUser;
import com.ttallang.user.mypage.service.FaultReportService;
import com.ttallang.user.mypage.service.RentalService;
import com.ttallang.user.mypage.service.UserService;
import com.ttallang.user.security.config.auth.PrincipalDetails;
import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/myPage")
public class MyPageRestController {

    private final UserService userService;
    private final RentalService rentalService;
    private final FaultReportService faultReportService;

    public MyPageRestController(UserService userService, RentalService rentalService,
        FaultReportService faultReportService) {
        this.userService = userService;
        this.rentalService = rentalService;
        this.faultReportService = faultReportService;
    }

    // 회원 정보 조회
    @GetMapping("/modify")
    public JoinUser getUserById(){
        PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loginId = pds.getCustomerID();
        return userService.getByUserId(loginId);
    }

    // 회원 정보 수정
    @PutMapping("/modify")
    public User updateUser(@RequestBody User updateUser){
        PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loginId = pds.getCustomerID();
        return userService.updateUser(loginId, updateUser);
    }

    // 회원 탈퇴
    @PatchMapping("/modify")
    public Roles deleteUser(){
        PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loginId = pds.getUserId();
        return userService.deleteUser(loginId);
    }

    // 이용 내역
    @GetMapping("/rental")
    public List<JoinBicycle> getRental(){
        PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loginId = pds.getCustomerID();
        return rentalService.getByRental(loginId);
    }

    // 신고 내역
    @GetMapping("/faultReport")
    public List<JoinFault> getFaultReport(){
        PrincipalDetails pds = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loginId = pds.getCustomerID();
        return faultReportService.findByFaultReport(loginId);
    }

    // 신고 내역 삭제
    @PatchMapping("/faultReport/{reportId}")
    public FaultReport deleteFaultReport(@PathVariable int reportId) {
        return faultReportService.deleteFaultReport(reportId);
    }
}
