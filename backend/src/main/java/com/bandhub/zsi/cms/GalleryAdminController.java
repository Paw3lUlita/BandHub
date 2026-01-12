package com.bandhub.zsi.cms;

import com.bandhub.zsi.cms.dto.GalleryImageResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/gallery")
@PreAuthorize("hasRole('ADMIN')")
class GalleryAdminController {

    private final GalleryAdminService service;

    GalleryAdminController(GalleryAdminService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<GalleryImageResponse>> getAll() {
        return ResponseEntity.ok(service.getAllImages());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Void> upload(@RequestParam("title") String title,
                                @RequestParam("file") MultipartFile file) {
        UUID id = service.uploadImage(title, file);
        return ResponseEntity.created(URI.create("/api/admin/gallery/" + id)).build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteImage(id);
        return ResponseEntity.noContent().build();
    }
}