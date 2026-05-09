package com.bandhub.zsi.cms;

import com.bandhub.zsi.cms.domain.SiteSettings;
import com.bandhub.zsi.cms.dto.SiteSettingsResponse;
import com.bandhub.zsi.cms.dto.UpdateSiteSettingsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SiteSettingsAdminService {

    static final String CACHE_NAME = "siteSettings";
    static final String CACHE_KEY = "singleton";

    private static final Logger log = LoggerFactory.getLogger(SiteSettingsAdminService.class);

    private final SiteSettingsRepository repository;

    public SiteSettingsAdminService(SiteSettingsRepository repository) {
        this.repository = repository;
    }

    @Cacheable(value = CACHE_NAME, key = "'" + CACHE_KEY + "'")
    @Transactional(readOnly = true)
    public SiteSettingsResponse getSettings() {
        SiteSettings settings = loadOrSeedSingleton();
        SiteSettingsResponse response = toResponse(settings);
        log.debug("Loaded site settings: bandName={}, hasTagline={}, hasHero={}, hasAbout={}, updatedAt={}",
                response.bandName(),
                response.tagline() != null,
                response.heroImageUrl() != null,
                response.aboutText() != null,
                response.updatedAt());
        return response;
    }

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public SiteSettingsResponse updateSettings(UpdateSiteSettingsRequest request, String updatedBy) {
        SiteSettings settings = loadOrSeedSingleton();
        settings.update(
                request.bandName(),
                request.tagline(),
                request.heroImageUrl(),
                request.aboutText(),
                updatedBy
        );
        return toResponse(repository.save(settings));
    }

    /**
     * Defensywne pobranie singletona - jezeli z jakichs powodow seed migracji
     * V15 nie zadzialal (np. baza zalozona przed migracja), tworzymy rekord
     * w locie zamiast 500/EntityNotFound. Dzieki temu admin moze otworzyc ekran
     * i zaczac edytowac wartosci.
     */
    private SiteSettings loadOrSeedSingleton() {
        return repository.findById(SiteSettings.SINGLETON_ID)
                .orElseGet(() -> {
                    log.warn("Brak rekordu site_settings z id={} - tworze domyslny singleton.",
                            SiteSettings.SINGLETON_ID);
                    return repository.save(SiteSettings.createDefault());
                });
    }

    private SiteSettingsResponse toResponse(SiteSettings settings) {
        return new SiteSettingsResponse(
                settings.getBandName(),
                settings.getTagline(),
                settings.getHeroImageUrl(),
                settings.getAboutText(),
                settings.getUpdatedAt(),
                settings.getUpdatedBy()
        );
    }
}
