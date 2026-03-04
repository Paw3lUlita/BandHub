-- Sprint 9 (lean): ticketing expansion for fan flows and validation

CREATE TABLE ticket_orders
(
    id           UUID PRIMARY KEY,
    user_id      VARCHAR(255)   NOT NULL,
    concert_id   UUID           NOT NULL REFERENCES concerts (id),
    status       VARCHAR(50)    NOT NULL,
    total_amount NUMERIC(19, 2) NOT NULL,
    currency     VARCHAR(3) DEFAULT 'PLN',
    created_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ticket_order_items
(
    id              UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    ticket_order_id UUID           NOT NULL REFERENCES ticket_orders (id) ON DELETE CASCADE,
    ticket_pool_id  UUID           NOT NULL REFERENCES ticket_pools (id),
    quantity        INT            NOT NULL,
    unit_price      NUMERIC(19, 2) NOT NULL,
    currency        VARCHAR(3) DEFAULT 'PLN'
);

CREATE TABLE ticket_codes
(
    id           UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    ticket_id    UUID           NOT NULL REFERENCES tickets (id) ON DELETE CASCADE,
    code_value   VARCHAR(120)   NOT NULL UNIQUE,
    code_type    VARCHAR(30)    NOT NULL DEFAULT 'QR',
    status       VARCHAR(30)    NOT NULL DEFAULT 'ACTIVE',
    generated_at TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activated_at TIMESTAMP,
    used_at      TIMESTAMP
);

CREATE TABLE ticket_validations
(
    id                UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    ticket_code_id    UUID           NOT NULL REFERENCES ticket_codes (id) ON DELETE CASCADE,
    validated_by      VARCHAR(255),
    gate_name         VARCHAR(100),
    validation_result VARCHAR(30)    NOT NULL,
    validation_time   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    details           VARCHAR(500)
);

CREATE TABLE ticket_refunds
(
    id              UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    ticket_order_id UUID REFERENCES ticket_orders (id),
    ticket_id       UUID REFERENCES tickets (id),
    amount          NUMERIC(19, 2) NOT NULL,
    currency        VARCHAR(3) DEFAULT 'PLN',
    reason          VARCHAR(500),
    status          VARCHAR(30)    NOT NULL DEFAULT 'REQUESTED',
    requested_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at     TIMESTAMP
);

CREATE INDEX idx_ticket_orders_user_id ON ticket_orders (user_id);
CREATE INDEX idx_ticket_orders_concert_id ON ticket_orders (concert_id);
CREATE INDEX idx_ticket_orders_status ON ticket_orders (status);
CREATE INDEX idx_ticket_order_items_order_id ON ticket_order_items (ticket_order_id);
CREATE INDEX idx_ticket_order_items_pool_id ON ticket_order_items (ticket_pool_id);
CREATE INDEX idx_ticket_codes_ticket_id ON ticket_codes (ticket_id);
CREATE INDEX idx_ticket_codes_status ON ticket_codes (status);
CREATE INDEX idx_ticket_validations_code_id ON ticket_validations (ticket_code_id);
CREATE INDEX idx_ticket_validations_time ON ticket_validations (validation_time);
CREATE INDEX idx_ticket_refunds_order_id ON ticket_refunds (ticket_order_id);
CREATE INDEX idx_ticket_refunds_status ON ticket_refunds (status);
