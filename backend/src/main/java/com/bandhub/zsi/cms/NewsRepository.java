package com.bandhub.zsi.cms;

import com.bandhub.zsi.cms.domain.NewsArticle;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NewsRepository {
    NewsArticle save(NewsArticle news);
    Optional<NewsArticle> findById(UUID id);
    List<NewsArticle> findAllByOrderByPublishedDateDesc();
    void deleteById(UUID id);
}
