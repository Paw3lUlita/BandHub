package com.bandhub.zsi.reporting.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "export_jobs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExportJob {

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

    public static ExportJob create(String module, String entityName, String status, String requestedBy, String filePath, LocalDateTime completedAt) {
        ExportJob job = new ExportJob();
        job.module = module;
        job.entityName = entityName;
        job.status = status;
        job.requestedBy = requestedBy;
        job.filePath = filePath;
        job.createdAt = LocalDateTime.now();
        job.completedAt = completedAt;
        return job;
    }

    public void update(String module, String entityName, String status, String requestedBy, String filePath, LocalDateTime completedAt) {
        this.module = module;
        this.entityName = entityName;
        this.status = status;
        this.requestedBy = requestedBy;
        this.filePath = filePath;
        this.completedAt = completedAt;
    }
}
