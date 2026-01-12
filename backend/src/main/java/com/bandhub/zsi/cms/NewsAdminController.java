package com.bandhub.zsi.cms;

import com.bandhub.zsi.cms.dto.CreateNewsRequest;
import com.bandhub.zsi.cms.dto.NewsResponse;
import com.bandhub.zsi.cms.dto.UpdateNewsCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/news")
@PreAuthorize("hasRole('ADMIN')") // Tylko dla managera
class NewsAdminController {

    private final CmsAdminService service;

    NewsAdminController(CmsAdminService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<NewsResponse>> getAll() {
        return ResponseEntity.ok(service.getAllNews());
    }

    @GetMapping("/{id}")
    ResponseEntity<NewsResponse> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getNews(id));
    }

    @PostMapping
    ResponseEntity<Void> create(@RequestBody CreateNewsRequest request, Authentication auth) {
        // Pobieramy ID zalogowanego managera (np. z tokena JWT)
        String authorId = auth.getName();
        UUID id = service.publishNews(request, authorId);
        return ResponseEntity.created(URI.create("/api/admin/news/" + id)).build();
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody UpdateNewsCommand command) {
        service.updateNews(id, command);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteNews(id);
        return ResponseEntity.noContent().build();
    }
}