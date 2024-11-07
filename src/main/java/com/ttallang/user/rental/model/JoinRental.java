package com.ttallang.user.rental.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JoinRental {

    private int bicycleId; // 자전거 ID 추가
    private String bicycleName; // 자전거 이름
    private String rentalBranch; // 대여 지점 이름
    private LocalDateTime rentalStartDate; // 대여 시작 시각
}
