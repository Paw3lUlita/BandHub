-- V15__Site_Settings.sql
-- Singleton z brandingiem zarzadzanym z panelu admina (mobile fan app czyta przez /api/public/site-settings).

CREATE TABLE site_settings
(
    id              SMALLINT PRIMARY KEY,
    band_name       VARCHAR(255) NOT NULL,
    tagline         VARCHAR(500),
    hero_image_url  VARCHAR(500),
    about_text      TEXT,
    updated_at      TIMESTAMP    NOT NULL,
    updated_by      VARCHAR(255)
);

INSERT INTO site_settings (id, band_name, tagline, hero_image_url, about_text, updated_at)
VALUES (1, 'BandHub', 'Twoje miejsce do koncertow i merchu', NULL,
        'Skonfiguruj te tresci w panelu administracyjnym (Ustawienia strony).', NOW());
