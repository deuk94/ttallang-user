package com.ttallang.user.mypage.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinFault {

    private int reportId;
    private String categoryName;
    private String reportDetails;
    private LocalDateTime reportDate;
    private String reportStatus;
    private String FaultStatus;
}
