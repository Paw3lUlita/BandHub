package com.bandhub.zsi.cms;

import com.bandhub.zsi.cms.dto.NewsResponse;
import com.bandhub.zsi.shared.api.PageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/public/news")
class NewsPublicController {

    private final CmsAdminService service;

    NewsPublicController(CmsAdminService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<NewsResponse>> getAll() {
        return ResponseEntity.ok(service.getAllNews());
    }

    @GetMapping("/page")
    ResponseEntity<PageResponse<NewsResponse>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "publishedDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "") String q
    ) {
        return ResponseEntity.ok(service.getNewsPage(page, size, sortBy, sortDir, q));
    }

    @GetMapping("/{id}")
    ResponseEntity<NewsResponse> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getNews(id));
    }
}
