package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.reporting.DocxTemplateRepository;
import com.bandhub.zsi.reporting.domain.DocxTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class SqlDocxTemplateRepository implements DocxTemplateRepository {

    private final JpaDocxTemplateRepository jpa;

    SqlDocxTemplateRepository(JpaDocxTemplateRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public DocxTemplate save(DocxTemplate template) {
        return jpa.save(template);
    }

    @Override
    public Optional<DocxTemplate> findById(UUID id) {
        return jpa.findById(id);
    }

    @Override
    public List<DocxTemplate> findAllOrderByCreatedAtDesc() {
        return jpa.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public List<DocxTemplate> findByModuleCodeOrderByCreatedAtDesc(String moduleCode) {
        return jpa.findByModuleCodeOrderByCreatedAtDesc(moduleCode);
    }

    @Override
    public Optional<DocxTemplate> findActiveByModuleCode(String moduleCode) {
        return jpa.findByModuleCodeAndActiveIsTrue(moduleCode);
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }
}

interface JpaDocxTemplateRepository extends JpaRepository<DocxTemplate, UUID> {

    List<DocxTemplate> findAllByOrderByCreatedAtDesc();

    List<DocxTemplate> findByModuleCodeOrderByCreatedAtDesc(String moduleCode);

    Optional<DocxTemplate> findByModuleCodeAndActiveIsTrue(String moduleCode);
}
