-- V5__Add_CMS_Module.sql

ALTER TABLE news_articles
    ADD COLUMN image_url VARCHAR(500);

CREATE TABLE gallery_images
(
    id          UUID PRIMARY KEY,
    title       VARCHAR(255),
    image_url   VARCHAR(500) NOT NULL,
    uploaded_at TIMESTAMP    NOT NULL
);