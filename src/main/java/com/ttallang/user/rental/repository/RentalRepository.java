package com.ttallang.user.rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ttallang.user.commonModel.Rental;

import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Integer> {

    // 특정 사용자가 현재 대여 중인지 확인하는 메서드
    boolean existsByCustomerIdAndRentalStatus(int customerId, String rentalStatus);

    // 특정 사용자의 대여 중인 렌탈 정보 조회
    Optional<Rental> findByCustomerIdAndRentalStatus(int customerId, String rentalStatus);
}
