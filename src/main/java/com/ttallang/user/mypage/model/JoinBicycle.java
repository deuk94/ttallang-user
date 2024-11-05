package com.ttallang.user.mypage.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JoinBicycle {

    private String bicycleName;
    private String rentalBranch;
    private LocalDateTime rentalStartDate;
    private String returnBranch;
    private LocalDateTime rentalEndDate;
}
