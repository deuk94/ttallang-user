package com.ttallang.user.rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ttallang.user.commonModel.Rental;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Integer> {

    List<Rental> findByCustomerId(int customerId);

    List<Rental> findByCustomerIdAndRentalEndDateIsNull(int customerId);
}
