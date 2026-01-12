package com.bandhub.zsi.cms;

import com.bandhub.zsi.cms.domain.GalleryImage;
import com.bandhub.zsi.cms.dto.GalleryImageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class GalleryAdminService {

    private final GalleryRepository repository;
    private final Path fileStorageLocation;

    // Wstrzykujemy ścieżkę z application.properties
    public GalleryAdminService(GalleryRepository repository, @Value("${app.upload.dir}") String uploadDir) {
        this.repository = repository;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Nie można utworzyć katalogu na pliki.", ex);
        }
    }

    public UUID uploadImage(String title, MultipartFile file) {
        // 1. Walidacja nazwy pliku
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.contains("..")) {
            throw new IllegalArgumentException("Nieprawidłowa nazwa pliku: " + originalFileName);
        }

        // 2. Generowanie unikalnej nazwy (UUID + rozszerzenie)
        String extension = "";
        int i = originalFileName.lastIndexOf('.');
        if (i > 0) {
            extension = originalFileName.substring(i);
        }
        String storedFileName = UUID.randomUUID().toString() + extension;

        // 3. Zapis fizyczny na dysku
        try {
            Path targetLocation = this.fileStorageLocation.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new RuntimeException("Błąd zapisu pliku " + storedFileName, ex);
        }

        // 4. Zapis w bazie danych
        GalleryImage image = GalleryImage.upload(title, storedFileName);
        return repository.save(image).getId();
    }

    @Transactional(readOnly = true)
    public List<GalleryImageResponse> getAllImages() {
        return repository.findAllByOrderByUploadedAtDesc().stream()
                .map(img -> new GalleryImageResponse(
                        img.getId(),
                        img.getTitle(),
                        "/api/public/uploads/" + img.getImageUrl(), // Generujemy URL dostępny dla frontendu
                        img.getUploadedAt()
                ))
                .toList();
    }

    public void deleteImage(UUID id) {
        GalleryImage image = repository.findById(id);

        // 1. Usuń plik z dysku
        try {
            Path filePath = this.fileStorageLocation.resolve(image.getImageUrl());
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            // Logujemy błąd, ale nie przerywamy procesu usuwania z bazy
            System.err.println("Nie udało się usunąć pliku: " + ex.getMessage());
        }

        // 2. Usuń z bazy
        repository.deleteById(id);
    }
}
