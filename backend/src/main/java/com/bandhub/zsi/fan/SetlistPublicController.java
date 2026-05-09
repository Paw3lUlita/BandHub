package com.bandhub.zsi.fan;

import com.bandhub.zsi.fan.dto.SetlistItemResponse;
import com.bandhub.zsi.fan.dto.SetlistResponse;
import com.bandhub.zsi.shared.api.PageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/public/setlists")
class SetlistPublicController {

    private final SetlistAdminService setlistService;
    private final SetlistItemAdminService setlistItemService;

    SetlistPublicController(SetlistAdminService setlistService, SetlistItemAdminService setlistItemService) {
        this.setlistService = setlistService;
        this.setlistItemService = setlistItemService;
    }

    @GetMapping
    ResponseEntity<List<SetlistResponse>> getAll() {
        return ResponseEntity.ok(setlistService.getAll());
    }

    @GetMapping("/page")
    ResponseEntity<PageResponse<SetlistResponse>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "") String q
    ) {
        return ResponseEntity.ok(setlistService.getPage(page, size, sortBy, sortDir, q));
    }

    @GetMapping("/{id}")
    ResponseEntity<SetlistResponse> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(setlistService.getOne(id));
    }

    @GetMapping("/{id}/items")
    ResponseEntity<List<SetlistItemResponse>> getItems(@PathVariable UUID id) {
        return ResponseEntity.ok(setlistItemService.getBySetlistId(id));
    }
}
