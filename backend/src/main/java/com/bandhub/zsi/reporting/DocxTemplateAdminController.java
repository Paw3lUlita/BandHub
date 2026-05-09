package com.bandhub.zsi.reporting;

import com.bandhub.zsi.reporting.dto.DocxTemplateResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * REST: zarządzanie szablonami DOCX (upload, lista, aktywacja).
 */
@RestController
@RequestMapping("/api/admin/reports/docx-templates")
@PreAuthorize("hasRole('ADMIN')")
class DocxTemplateAdminController {

    private final DocxTemplateAdminService service;

    DocxTemplateAdminController(DocxTemplateAdminService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<DocxTemplateResponse>> list(@RequestParam(required = false) String moduleCode) {
        if (moduleCode != null && !moduleCode.isBlank()) {
            return ResponseEntity.ok(service.listByModule(moduleCode.trim()));
        }
        return ResponseEntity.ok(service.listAll());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<DocxTemplateResponse> upload(
            @RequestParam("name") String name,
            @RequestParam("moduleCode") String moduleCode,
            @RequestParam("file") MultipartFile file
    ) {
        DocxTemplateResponse body = service.upload(name, moduleCode, file);
        return ResponseEntity.created(URI.create("/api/admin/reports/docx-templates/" + body.id())).body(body);
    }

    @PatchMapping("/{id}/activate")
    ResponseEntity<DocxTemplateResponse> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(service.activate(id));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
