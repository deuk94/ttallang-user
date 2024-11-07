package com.ttallang.user.commomRepository;

import com.ttallang.user.commonModel.Rental;
import com.ttallang.user.mypage.model.JoinBicycle;
import com.ttallang.user.rental.model.JoinRental;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentalRepository extends JpaRepository<Rental, Integer> {

    @Query("SELECT new com.ttallang.user.mypage.model.JoinBicycle(b.bicycleName, " +
        "r.rentalBranch, r.rentalStartDate, r.returnBranch, r.rentalEndDate) " +
        "FROM Rental r JOIN Bicycle b ON b.bicycleId = r.bicycleId " +
        "WHERE r.customerId = :customerId " +
        "ORDER BY r.rentalStartDate DESC ")
    List<JoinBicycle> getByRentalId(@Param("customerId") int customerId);

    List<Rental> findByCustomerIdAndRentalEndDateIsNull(int customerId);


}