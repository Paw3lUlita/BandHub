package com.bandhub.zsi.cms;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Publiczny endpoint zwracajacy plaska mape klucz->wartosc dla mobilki/admin.
 * Pobierany na starcie aplikacji (APP_INITIALIZER w Angularze, BrandingProvider w mobilce).
 */
@RestController
@RequestMapping("/api/public/ui-dictionary")
class UiDictionaryPublicController {

    private final UiDictionaryService service;

    UiDictionaryPublicController(UiDictionaryService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<Map<String, String>> getFlatDictionary() {
        return ResponseEntity.ok(service.getFlatDictionary());
    }
}
