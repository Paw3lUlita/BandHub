package com.bandhub.zsi.fan;

import com.bandhub.zsi.fan.domain.Setlist;
import com.bandhub.zsi.fan.dto.CreateSetlistRequest;
import com.bandhub.zsi.fan.dto.SetlistResponse;
import com.bandhub.zsi.fan.dto.UpdateSetlistRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import com.bandhub.zsi.ticketing.ConcertRepository;
import com.bandhub.zsi.ticketing.domain.Concert;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SetlistAdminService {

    private final SetlistRepository setlistRepository;
    private final ConcertRepository concertRepository;

    public SetlistAdminService(SetlistRepository setlistRepository, ConcertRepository concertRepository) {
        this.setlistRepository = setlistRepository;
        this.concertRepository = concertRepository;
    }

    public UUID create(CreateSetlistRequest request) {
        Concert concert = concertRepository.findById(request.concertId())
                .orElseThrow(() -> new EntityNotFoundException("Concert not found: " + request.concertId()));

        Setlist setlist = Setlist.create(concert, request.title(), request.createdBy(), request.publishedAt());
        return setlistRepository.save(setlist).getId();
    }

    public void update(UUID id, UpdateSetlistRequest request) {
        Setlist setlist = setlistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Setlist not found: " + id));
        setlist.update(request.title(), request.publishedAt());
    }

    public void delete(UUID id) {
        if (setlistRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Setlist not found: " + id);
        }
        setlistRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public SetlistResponse getOne(UUID id) {
        return setlistRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Setlist not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<SetlistResponse> getAll() {
        return setlistRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<SetlistResponse> getPage(int page, int size, String sortBy, String sortDir, String query) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase();
        boolean descending = "desc".equalsIgnoreCase(sortDir);

        List<SetlistResponse> filtered = setlistRepository.findAll().stream()
                .map(this::toResponse)
                .filter(setlist -> normalizedQuery.isBlank()
                        || setlist.title().toLowerCase().contains(normalizedQuery)
                        || setlist.concertName().toLowerCase().contains(normalizedQuery)
                        || (setlist.createdBy() != null && setlist.createdBy().toLowerCase().contains(normalizedQuery)))
                .sorted(resolveComparator(sortBy, descending))
                .toList();

        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int fromIndex = safePage * safeSize;
        int toIndex = Math.min(fromIndex + safeSize, filtered.size());
        List<SetlistResponse> content = fromIndex >= filtered.size()
                ? List.of()
                : filtered.subList(fromIndex, toIndex);

        return PageResponse.of(content, safePage, safeSize, filtered.size(), sortBy, sortDir, query);
    }

    private Comparator<SetlistResponse> resolveComparator(String sortBy, boolean descending) {
        Comparator<SetlistResponse> comparator = switch (sortBy) {
            case "title" -> Comparator.comparing(SetlistResponse::title, String.CASE_INSENSITIVE_ORDER);
            case "concertName" -> Comparator.comparing(SetlistResponse::concertName, String.CASE_INSENSITIVE_ORDER);
            case "publishedAt" -> Comparator.comparing(SetlistResponse::publishedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            case "createdAt" -> Comparator.comparing(SetlistResponse::createdAt, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(SetlistResponse::createdAt, Comparator.nullsLast(Comparator.naturalOrder()));
        };
        return descending ? comparator.reversed() : comparator;
    }

    private SetlistResponse toResponse(Setlist setlist) {
        return new SetlistResponse(
                setlist.getId(),
                setlist.getConcert().getId(),
                setlist.getConcert().getName(),
                setlist.getTitle(),
                setlist.getCreatedBy(),
                setlist.getPublishedAt(),
                setlist.getCreatedAt()
        );
    }
}
