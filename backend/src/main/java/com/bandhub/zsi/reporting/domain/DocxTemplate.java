package com.bandhub.zsi.reporting.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "docx_templates")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocxTemplate {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "module_code", nullable = false)
    private String moduleCode;

    @Column(name = "template_version", nullable = false)
    private int templateVersion;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static DocxTemplate create(String name, String moduleCode, int templateVersion, boolean active, String filePath) {
        DocxTemplate t = new DocxTemplate();
        t.name = name;
        t.moduleCode = moduleCode;
        t.templateVersion = templateVersion;
        t.active = active;
        t.filePath = filePath;
        t.createdAt = LocalDateTime.now();
        return t;
    }

    public void rename(String newName) {
        this.name = newName;
    }

    public void setActive(boolean value) {
        this.active = value;
    }
}
