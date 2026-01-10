-- V3__Auto_Gen_OrderItem_Id.sql

ALTER TABLE order_items
    ALTER COLUMN id SET DEFAULT gen_random_uuid();