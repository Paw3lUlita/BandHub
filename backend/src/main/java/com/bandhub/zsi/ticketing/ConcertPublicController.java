package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.fan.SetlistAdminService;
import com.bandhub.zsi.fan.dto.SetlistResponse;
import com.bandhub.zsi.ticketing.dto.ConcertDetailResponse;
import com.bandhub.zsi.ticketing.dto.ConcertResponse;
import com.bandhub.zsi.shared.api.PageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/public/concerts")
class ConcertPublicController {

    private final ConcertPublicService service;
    private final SetlistAdminService setlistAdminService;

    ConcertPublicController(ConcertPublicService service, SetlistAdminService setlistAdminService) {
        this.service = service;
        this.setlistAdminService = setlistAdminService;
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

    @GetMapping("/{id}/setlists")
    ResponseEntity<List<SetlistResponse>> getSetlists(@PathVariable UUID id) {
        return ResponseEntity.ok(setlistAdminService.getPublishedByConcertId(id));
    }
}
