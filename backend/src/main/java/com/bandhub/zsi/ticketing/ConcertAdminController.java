package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.ticketing.dto.ConcertDetailResponse;
import com.bandhub.zsi.ticketing.dto.ConcertResponse;
import com.bandhub.zsi.ticketing.dto.CreateConcertRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/concerts")
@PreAuthorize("hasRole('ADMIN')")
class ConcertAdminController {

    private final ConcertAdminService service;

    ConcertAdminController(ConcertAdminService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<Void> create(@RequestBody @Valid CreateConcertRequest request) {
        UUID id = service.createConcert(request);
        return ResponseEntity.created(URI.create("/api/admin/concerts/" + id)).build();
    }

    @GetMapping
    ResponseEntity<List<ConcertResponse>> getAll() {
        return ResponseEntity.ok(service.getAllConcerts());
    }

    @GetMapping("/page")
    ResponseEntity<PageResponse<ConcertResponse>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "") String q
    ) {
        return ResponseEntity.ok(service.getConcertsPage(page, size, sortBy, sortDir, q));
    }

    @GetMapping("/{id}")
    ResponseEntity<ConcertDetailResponse> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getConcert(id));
    }

}