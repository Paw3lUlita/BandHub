-- V4__Update_Ticketing.sql

ALTER TABLE venues RENAME COLUMN address TO street;

ALTER TABLE venues ADD COLUMN contact_email VARCHAR(255);

ALTER TABLE concerts ADD COLUMN description VARCHAR(2000);

ALTER TABLE concerts ADD COLUMN image_url VARCHAR(500);