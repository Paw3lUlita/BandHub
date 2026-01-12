package com.bandhub.zsi.cms;

import com.bandhub.zsi.cms.domain.NewsArticle;
import com.bandhub.zsi.cms.dto.CreateNewsRequest;
import com.bandhub.zsi.cms.dto.NewsResponse;
import com.bandhub.zsi.cms.dto.UpdateNewsCommand;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CmsAdminService {

    private final NewsRepository newsRepository;

    public CmsAdminService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public UUID publishNews(CreateNewsRequest request, String authorId) {
        NewsArticle news = NewsArticle.publish(
                request.title(),
                request.content(),
                request.imageUrl(),
                authorId
        );
        return newsRepository.save(news).getId();
    }

    public void updateNews(UUID id, UpdateNewsCommand command) {
        NewsArticle news = newsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("News not found: " + id));

        news.updateContent(command.title(), command.content(), command.imageUrl());
        // Dirty Checking zadziała automatycznie
    }

    public void deleteNews(UUID id) {
        newsRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<NewsResponse> getAllNews() {
        return newsRepository.findAllByOrderByPublishedDateDesc().stream()
                .map(n -> new NewsResponse(
                        n.getId(),
                        n.getTitle(),
                        n.getContent(),
                        n.getImageUrl(),
                        n.getPublishedDate(),
                        n.getAuthorId()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public NewsResponse getNews(UUID id) {
        return newsRepository.findById(id)
                .map(n -> new NewsResponse(
                        n.getId(),
                        n.getTitle(),
                        n.getContent(),
                        n.getImageUrl(),
                        n.getPublishedDate(),
                        n.getAuthorId()
                ))
                .orElseThrow(() -> new EntityNotFoundException("News not found"));
    }
}
