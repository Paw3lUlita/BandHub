package com.bandhub.zsi.cms;

import com.bandhub.zsi.cms.dto.CreateUiDictionaryEntryRequest;
import com.bandhub.zsi.cms.dto.UiDictionaryEntryResponse;
import com.bandhub.zsi.cms.dto.UpdateUiDictionaryEntryRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/ui-dictionary")
@PreAuthorize("hasRole('ADMIN')")
class UiDictionaryAdminController {

    private final UiDictionaryService service;

    UiDictionaryAdminController(UiDictionaryService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<UiDictionaryEntryResponse>> list() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{key}")
    ResponseEntity<UiDictionaryEntryResponse> get(@PathVariable String key) {
        return ResponseEntity.ok(service.getOne(key));
    }

    @PostMapping
    ResponseEntity<UiDictionaryEntryResponse> create(
            @RequestBody @Valid CreateUiDictionaryEntryRequest request,
            Authentication authentication
    ) {
        String updatedBy = authentication != null ? authentication.getName() : null;
        UiDictionaryEntryResponse created = service.create(request, updatedBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{key}")
    ResponseEntity<UiDictionaryEntryResponse> update(
            @PathVariable String key,
            @RequestBody @Valid UpdateUiDictionaryEntryRequest request,
            Authentication authentication
    ) {
        String updatedBy = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(service.update(key, request, updatedBy));
    }

    @DeleteMapping("/{key}")
    ResponseEntity<Void> delete(@PathVariable String key) {
        service.delete(key);
        return ResponseEntity.noContent().build();
    }
}
