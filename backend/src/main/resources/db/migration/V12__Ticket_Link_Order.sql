-- Link issued tickets (tickets table) to ticket_orders for attendees / reporting

ALTER TABLE tickets
    ADD COLUMN ticket_order_id UUID REFERENCES ticket_orders (id);

CREATE INDEX idx_tickets_ticket_order_id ON tickets (ticket_order_id);
