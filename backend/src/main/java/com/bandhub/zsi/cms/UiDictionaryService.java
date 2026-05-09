package com.bandhub.zsi.cms;

import com.bandhub.zsi.cms.domain.UiDictionaryEntry;
import com.bandhub.zsi.cms.dto.CreateUiDictionaryEntryRequest;
import com.bandhub.zsi.cms.dto.UiDictionaryEntryResponse;
import com.bandhub.zsi.cms.dto.UpdateUiDictionaryEntryRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Slownik UI - kazdy widoczny w mobilce/admin tekst zarzadzany z bazy.
 * Cache `uiDictionary` minimalizuje hit-y do DB przy starcie kazdego klienta.
 */
@Service
@Transactional
public class UiDictionaryService {

    static final String CACHE_NAME = "uiDictionary";
    static final String FLAT_MAP_KEY = "flat";

    private final UiDictionaryRepository repository;

    public UiDictionaryService(UiDictionaryRepository repository) {
        this.repository = repository;
    }

    @Cacheable(value = CACHE_NAME, key = "'" + FLAT_MAP_KEY + "'")
    @Transactional(readOnly = true)
    public Map<String, String> getFlatDictionary() {
        Map<String, String> map = new LinkedHashMap<>();
        for (UiDictionaryEntry entry : repository.findAll()) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    @Transactional(readOnly = true)
    public List<UiDictionaryEntryResponse> getAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UiDictionaryEntryResponse getOne(String key) {
        return toResponse(loadEntry(key));
    }

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public UiDictionaryEntryResponse create(CreateUiDictionaryEntryRequest request, String updatedBy) {
        if (repository.existsById(request.key())) {
            throw new IllegalArgumentException("Klucz '" + request.key() + "' juz istnieje w slowniku");
        }
        UiDictionaryEntry entry = UiDictionaryEntry.create(
                request.key(),
                request.value(),
                request.description(),
                updatedBy
        );
        return toResponse(repository.save(entry));
    }

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public UiDictionaryEntryResponse update(String key, UpdateUiDictionaryEntryRequest request, String updatedBy) {
        UiDictionaryEntry entry = loadEntry(key);
        entry.update(request.value(), request.description(), updatedBy);
        return toResponse(repository.save(entry));
    }

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void delete(String key) {
        UiDictionaryEntry entry = loadEntry(key);
        repository.delete(entry);
    }

    private UiDictionaryEntry loadEntry(String key) {
        return repository.findById(key)
                .orElseThrow(() -> new EntityNotFoundException("Brak klucza w slowniku: " + key));
    }

    private UiDictionaryEntryResponse toResponse(UiDictionaryEntry entry) {
        return new UiDictionaryEntryResponse(
                entry.getKey(),
                entry.getValue(),
                entry.getDescription(),
                entry.getUpdatedAt(),
                entry.getUpdatedBy()
        );
    }
}
