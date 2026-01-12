package com.bandhub.zsi.cms;

import com.bandhub.zsi.cms.domain.GalleryImage;
import java.util.List;
import java.util.UUID;

public interface GalleryRepository {
    GalleryImage save(GalleryImage image);
    List<GalleryImage> findAllByOrderByUploadedAtDesc();
    void deleteById(UUID id);
    GalleryImage findById(UUID id);
}
