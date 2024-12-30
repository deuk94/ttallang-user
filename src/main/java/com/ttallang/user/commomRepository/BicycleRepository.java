package com.ttallang.user.commomRepository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.ttallang.user.commonModel.Bicycle;

public interface BicycleRepository extends JpaRepository<Bicycle, Integer> {

    // 대여소에 있는 대여 가능 자전거 리스트 조회
    @Query("SELECT b FROM Bicycle b " +
        "WHERE b.rentalStatus = '1' " +
        "AND b.bicycleStatus = '1' " +
        "AND b.reportStatus = '1' " +
        "AND (6371000 * ACOS(COS(RADIANS(:latitude)) * COS(RADIANS(b.latitude)) * COS(RADIANS(b.longitude) - RADIANS(:longitude)) + SIN(RADIANS(:latitude)) * SIN(RADIANS(b.latitude)))) < :distance")
    List<Bicycle> findAvailableBike(@Param("latitude") double latitude,
        @Param("longitude") double longitude, @Param("distance") double distance);

}
