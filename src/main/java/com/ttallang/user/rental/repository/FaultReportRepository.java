package com.ttallang.user.rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ttallang.user.commonModel.FaultReport;

public interface FaultReportRepository extends JpaRepository<FaultReport, Integer> {

}
