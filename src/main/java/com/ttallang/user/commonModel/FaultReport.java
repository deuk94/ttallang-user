package com.ttallang.user.commonModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Fault_report")
public class FaultReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // primary key 자동생성
    @Column(name = "report_id")
    private int reportId;

    @Column(name = "category_Id")
    private int categoryId;

    @Column(name = "bicycle_id")
    private int bicycleId;

    @Column(name = "customer_id")
    private int customerId;

    @Column(name = "report_date")
    private LocalDateTime reportDate;

    @Column(name = "report_details")
    private String reportDetails;

    @Column(name = "report_status")
    private String reportStatus;

    @Column(name = "fault_status")
    private String faultStatus;
}
