package com.bandhub.zsi.reporting;

import com.bandhub.zsi.reporting.domain.DocxTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocxTemplateRepository {
    DocxTemplate save(DocxTemplate template);

    Optional<DocxTemplate> findById(UUID id);

    List<DocxTemplate> findAllOrderByCreatedAtDesc();

    List<DocxTemplate> findByModuleCodeOrderByCreatedAtDesc(String moduleCode);

    Optional<DocxTemplate> findActiveByModuleCode(String moduleCode);

    void deleteById(UUID id);
}
