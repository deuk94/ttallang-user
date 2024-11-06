package com.ttallang.user.commomRepository;


import com.ttallang.user.commonModel.FaultReport;
import com.ttallang.user.mypage.model.JoinFault;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FaultReportRepository extends JpaRepository<FaultReport, Integer> {

    // 신고 내역 조회
    @Query("SELECT new com.ttallang.user.mypage.model.JoinFault(r.reportId, c.categoryName, "
        + "r.reportDetails, r.reportDate, r.reportStatus, r.faultStatus) "
        + "FROM FaultReport r Join FaultCategory c ON c.categoryId = r.categoryId "
        + "WHERE r.customerId = :customerId AND r.faultStatus = '1'"
        + "ORDER BY r.reportDate DESC ")
    List<JoinFault> findByFaultId(@Param("customerId") int customerId);
}
