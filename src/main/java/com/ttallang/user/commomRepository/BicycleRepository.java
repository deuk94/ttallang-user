package com.ttallang.user.commomRepository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.ttallang.user.commonModel.Bicycle;

public interface BicycleRepository extends JpaRepository<Bicycle, Integer> {

    // 대여소 반경 10m 이내 대여 가능 자전거 수
    @Query("SELECT COUNT(b) FROM Bicycle b " +
        "WHERE b.rentalStatus = '1' " + // 대여 가능 상태만 조회
        "AND b.bicycleStatus = '1' " +  // 운행 가능한 상태만 조회
        "AND b.reportStatus = '1' " +   // 신고 상태 처리 완료된 자전거만 조회
        "AND SQRT(POWER(b.latitude - :latitude, 2) + POWER(b.longitude - :longitude, 2)) < :distance")
    int findByBikeCount(@Param("latitude") double latitude,
        @Param("longitude") double longitude,
        @Param("distance") double distance);

    // 대여소에 있는 대여 가능 자전거 리스트 조회
    @Query("SELECT b FROM Bicycle b " +
        "WHERE b.rentalStatus = '1' " + // 대여 가능 상태만 조회
        "AND b.bicycleStatus = '1' " +  // 운행 가능한 상태만 조회
        "AND b.reportStatus = '1' " +   // 신고 상태 처리 완료된 자전거만 조회
        "AND SQRT(POWER(b.latitude - :latitude, 2) + POWER(b.longitude - :longitude, 2)) < :distance")
    List<Bicycle> findAvailableBike(@Param("latitude") double latitude,
        @Param("longitude") double longitude,
        @Param("distance") double distance);
}