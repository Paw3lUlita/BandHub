package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.cms.GalleryRepository;
import com.bandhub.zsi.cms.domain.GalleryImage;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class SqlGalleryRepository implements GalleryRepository {

    private final JpaGalleryRepository jpaRepository;

    public SqlGalleryRepository(JpaGalleryRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public GalleryImage save(GalleryImage image) {
        return jpaRepository.save(image);
    }

    @Override
    public List<GalleryImage> findAllByOrderByUploadedAtDesc() {
        return jpaRepository.findAllByOrderByUploadedAtDesc();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public GalleryImage findById(UUID id) {
        return jpaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image not found: " + id));
    }
}

interface JpaGalleryRepository extends JpaRepository<GalleryImage, UUID> {
    List<GalleryImage> findAllByOrderByUploadedAtDesc();
}
