package com.ttallang.user.commomRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ttallang.user.commonModel.Branch;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BranchRepository extends JpaRepository<Branch, Integer> {

    // branch_status가 1인 대여소만 조회하는 쿼리
    @Query("SELECT b FROM Branch b WHERE b.branchStatus = '1'")
    List<Branch> findActiveBranches();

    // 특정 위치에서 반경 내에 있는 대여소 이름 조회
    @Query("SELECT b.branchName FROM Branch b " +
        "WHERE b.branchStatus = '1' " +
        "AND SQRT(POWER(b.latitude - :latitude, 2) + POWER(b.longitude - :longitude, 2)) < :radius")
    Optional<String> findNearbyBranchName(@Param("latitude") double latitude,
        @Param("longitude") double longitude,
        @Param("radius") double radius); // radius는 0.0009로 설정

}
