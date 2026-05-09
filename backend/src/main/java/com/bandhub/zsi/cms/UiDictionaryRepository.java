package com.bandhub.zsi.cms;

import com.bandhub.zsi.cms.domain.UiDictionaryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

interface UiDictionaryRepository extends JpaRepository<UiDictionaryEntry, String> {
}
