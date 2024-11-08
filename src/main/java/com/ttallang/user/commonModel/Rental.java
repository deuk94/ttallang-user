package com.ttallang.user.commonModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rental")
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // primary key 자동생성
    @Column(name = "rental_id")
    private int rentalId;

    @Column(name = "bicycle_id")
    private int bicycleId;

    @Column(name = "customer_id")
    private int customerId;

    @Column(name = "rental_startdate")
    private LocalDateTime rentalStartDate;

    @Column(name = "rental_enddate")
    private LocalDateTime rentalEndDate;

    @Column(name = "rental_branch")
    private String rentalBranch;

    @Column(name = "return_branch")
    private String returnBranch;
}
