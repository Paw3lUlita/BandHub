package com.bandhub.zsi.cms;

import com.bandhub.zsi.cms.domain.SiteSettings;
import org.springframework.data.jpa.repository.JpaRepository;

interface SiteSettingsRepository extends JpaRepository<SiteSettings, Short> {
}
