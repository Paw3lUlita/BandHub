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
class ReportRun {

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
}

@Entity
@Table(name = "export_jobs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class ExportJob {

    @Id
    @GeneratedValue
    private UUID id;

    private String module;

    @Column(name = "entity_name")
    private String entityName;

    private String status;

    @Column(name = "requested_by")
    private String requestedBy;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
