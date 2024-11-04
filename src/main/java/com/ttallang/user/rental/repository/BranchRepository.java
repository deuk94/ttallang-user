package com.ttallang.user.rental.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ttallang.user.commonModel.Branch;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BranchRepository extends JpaRepository<Branch, Integer> {

    // 특정 위치 반경 내의 대여소를 조회하는 쿼리
    @Query("SELECT b FROM Branch b WHERE SQRT(POWER(b.latitude - :latitude, 2) + POWER(b.longitude - :longitude, 2)) < :distance")
    List<Branch> findNearbyBranches(
        @Param("latitude") double latitude, @Param("longitude") double longitude, @Param("distance") double distance
    );

}
