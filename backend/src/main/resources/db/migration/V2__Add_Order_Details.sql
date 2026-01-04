-- V2__Add_Order_Details.sql

ALTER TABLE orders
    ADD COLUMN currency VARCHAR(3) DEFAULT 'PLN';

ALTER TABLE order_items
    ADD COLUMN product_name VARCHAR(255);

ALTER TABLE order_items
    ADD COLUMN currency VARCHAR(3) DEFAULT 'PLN';
