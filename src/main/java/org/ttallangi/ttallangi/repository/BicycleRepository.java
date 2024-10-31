package org.ttallangi.ttallangi.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.ttallangi.ttallangi.model.Bicycle;

public interface BicycleRepository extends JpaRepository<Bicycle, Integer> {

    // 대여소 반경 10m이내 자전거 수
    @Query("SELECT COUNT(b) FROM Bicycle b " +
        "WHERE b.bicycleStatus = '0' " + // 대여 가능 상태를 "0"으로 조회
        "AND SQRT(POWER(b.latitude - :latitude, 2) + POWER(b.longitude - :longitude, 2)) < :distance")
    int findByBikeCount(@Param("latitude") double latitude,
        @Param("longitude") double longitude, @Param("distance") double distance);

    // 대여소에 0 = 대여 가능인 자전거를 리스트로
    @Query("SELECT b FROM Bicycle b " +
        "WHERE b.bicycleStatus = '0' " + // 대여 가능 상태를 "0"으로 조회
        "AND SQRT(POWER(b.latitude - :latitude, 2) + POWER(b.longitude - :longitude, 2)) < :distance")
    List<Bicycle> findAvailableBike(@Param("latitude") double latitude,
        @Param("longitude") double longitude, @Param("distance") double distance);
}
