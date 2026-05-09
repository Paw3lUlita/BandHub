package com.bandhub.zsi.cms;

import com.bandhub.zsi.cms.dto.GalleryImageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/gallery")
class GalleryFeedPublicController {

    private final GalleryAdminService service;

    GalleryFeedPublicController(GalleryAdminService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<GalleryImageResponse>> getAll() {
        return ResponseEntity.ok(service.getAllImages());
    }
}
