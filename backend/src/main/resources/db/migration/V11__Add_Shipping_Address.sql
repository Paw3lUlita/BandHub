-- Add delivery address to shipments for checkout flow (lean - single text field)
ALTER TABLE shipments
    ADD COLUMN delivery_address VARCHAR(500);
