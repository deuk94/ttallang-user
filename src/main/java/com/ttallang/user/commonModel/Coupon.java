package com.ttallang.user.commonModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coupon")
public class Coupon {

    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // primary key 자동생성
    @Column(name = "coupon_id")
    private int couponId;

    @Column(name = "customer_id")
    private int customerId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "discount_type")
    private String discountType;

    @Column(name = "discount_value")
    private int discountValue;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "min_rental_time")
    private int minRentalTime;

    @Column(name = "is_used")
    private String isUsed;

    @Column(name = "created_at")
    private String createdAt;
}