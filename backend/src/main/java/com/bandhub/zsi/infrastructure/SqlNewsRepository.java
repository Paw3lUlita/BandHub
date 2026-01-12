package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.cms.NewsRepository; // Importujemy Twój interfejs domenowy
import com.bandhub.zsi.cms.domain.NewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SqlNewsRepository implements NewsRepository {

    private final JpaNewsRepository jpaRepository;

    public SqlNewsRepository(JpaNewsRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public NewsArticle save(NewsArticle news) {
        return jpaRepository.save(news);
    }

    @Override
    public Optional<NewsArticle> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<NewsArticle> findAllByOrderByPublishedDateDesc() {
        return jpaRepository.findAllByOrderByPublishedDateDesc();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

interface JpaNewsRepository extends JpaRepository<NewsArticle, UUID> {
    List<NewsArticle> findAllByOrderByPublishedDateDesc();
}