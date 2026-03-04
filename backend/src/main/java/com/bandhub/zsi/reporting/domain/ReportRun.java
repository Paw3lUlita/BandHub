package com.bandhub.zsi.reporting.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "report_runs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportRun {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "report_name")
    private String reportName;

    @Column(name = "requested_by")
    private String requestedBy;

    @Column(name = "parameters_json", columnDefinition = "TEXT")
    private String parametersJson;

    private String status;

    @Column(name = "file_format")
    private String fileFormat;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public static ReportRun create(String reportName, String requestedBy, String parametersJson, String status, String fileFormat, LocalDateTime completedAt) {
        ReportRun run = new ReportRun();
        run.reportName = reportName;
        run.requestedBy = requestedBy;
        run.parametersJson = parametersJson;
        run.status = status;
        run.fileFormat = fileFormat;
        run.createdAt = LocalDateTime.now();
        run.completedAt = completedAt;
        return run;
    }

    public void update(String reportName, String requestedBy, String parametersJson, String status, String fileFormat, LocalDateTime completedAt) {
        this.reportName = reportName;
        this.requestedBy = requestedBy;
        this.parametersJson = parametersJson;
        this.status = status;
        this.fileFormat = fileFormat;
        this.completedAt = completedAt;
    }
}
