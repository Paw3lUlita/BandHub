package com.bandhub.zsi.reporting;

import com.bandhub.zsi.reporting.domain.DocxTemplate;
import com.bandhub.zsi.reporting.dto.DocxTemplateResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * Upload i aktywacja szablonów DOCX (katalog {@code app.upload.dir}, podkatalog {@code docx-templates}).
 */
@Service
@Transactional
class DocxTemplateAdminService {

    private static final String DOCX_SUBDIR = "docx-templates";

    private final DocxTemplateRepository repository;
    private final Path fileStorageLocation;

    DocxTemplateAdminService(DocxTemplateRepository repository, @Value("${app.upload.dir}") String uploadDir) {
        this.repository = repository;
        Path root = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.fileStorageLocation = root.resolve(DOCX_SUBDIR).normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Cannot create DOCX template directory", ex);
        }
    }

    @Transactional(readOnly = true)
    public List<DocxTemplateResponse> listAll() {
        return repository.findAllOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<DocxTemplateResponse> listByModule(String moduleCode) {
        return repository.findByModuleCodeOrderByCreatedAtDesc(moduleCode).stream().map(this::toResponse).toList();
    }

    public DocxTemplateResponse upload(String name, String moduleCode, MultipartFile file) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (moduleCode == null || moduleCode.isBlank()) {
            throw new IllegalArgumentException("moduleCode is required");
        }
        String original = file.getOriginalFilename();
        if (original == null || original.contains("..")) {
            throw new IllegalArgumentException("Invalid file name");
        }
        String lower = original.toLowerCase();
        if (!lower.endsWith(".docx")) {
            throw new IllegalArgumentException("Only .docx files are allowed");
        }
        String ct = file.getContentType();
        if (ct != null && !ct.isBlank()
                && !ct.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            throw new IllegalArgumentException("Invalid content type for DOCX");
        }

        int nextVersion = repository.findByModuleCodeOrderByCreatedAtDesc(moduleCode).stream()
                .mapToInt(DocxTemplate::getTemplateVersion)
                .max()
                .orElse(0) + 1;

        boolean autoActive = repository.findByModuleCodeOrderByCreatedAtDesc(moduleCode).isEmpty();

        String stored = UUID.randomUUID() + ".docx";
        Path target = fileStorageLocation.resolve(stored).normalize();
        if (!target.startsWith(fileStorageLocation)) {
            throw new IllegalArgumentException("Invalid path");
        }
        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store DOCX template", e);
        }

        if (autoActive) {
            deactivateAllInModule(moduleCode);
        }

        DocxTemplate entity = DocxTemplate.create(name.trim(), moduleCode.trim(), nextVersion, autoActive, stored);
        return toResponse(repository.save(entity));
    }

    public DocxTemplateResponse activate(UUID id) {
        DocxTemplate t = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("DOCX template not found: " + id));
        deactivateAllInModule(t.getModuleCode());
        DocxTemplate fresh = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("DOCX template not found: " + id));
        fresh.setActive(true);
        return toResponse(repository.save(fresh));
    }

    public void delete(UUID id) {
        DocxTemplate t = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("DOCX template not found: " + id));
        if (t.isActive()) {
            throw new IllegalStateException("Cannot delete active template; activate another first");
        }
        Path path = fileStorageLocation.resolve(t.getFilePath()).normalize();
        if (path.startsWith(fileStorageLocation)) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete template file", e);
            }
        }
        repository.deleteById(id);
    }

    private void deactivateAllInModule(String moduleCode) {
        for (DocxTemplate o : repository.findByModuleCodeOrderByCreatedAtDesc(moduleCode)) {
            if (o.isActive()) {
                o.setActive(false);
                repository.save(o);
            }
        }
    }

    private DocxTemplateResponse toResponse(DocxTemplate t) {
        return new DocxTemplateResponse(
                t.getId(),
                t.getName(),
                t.getModuleCode(),
                t.getTemplateVersion(),
                t.isActive(),
                t.getFilePath(),
                t.getCreatedAt()
        );
    }
}
