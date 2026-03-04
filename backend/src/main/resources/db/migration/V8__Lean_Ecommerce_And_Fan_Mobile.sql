-- Sprint 9 (lean): ecommerce essentials + fan/mobile support

-- 1) E-commerce essentials
CREATE TABLE payments
(
    id                  UUID PRIMARY KEY,
    order_id            UUID           NOT NULL REFERENCES orders (id),
    provider            VARCHAR(100),
    provider_payment_id VARCHAR(255),
    status              VARCHAR(50)    NOT NULL,
    amount              NUMERIC(19, 2) NOT NULL,
    currency            VARCHAR(3) DEFAULT 'PLN',
    paid_at             TIMESTAMP,
    created_at          TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payment_transactions
(
    id              UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    payment_id      UUID           NOT NULL REFERENCES payments (id) ON DELETE CASCADE,
    event_type      VARCHAR(100)   NOT NULL,
    external_status VARCHAR(100),
    payload         TEXT,
    created_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE shipments
(
    id              UUID PRIMARY KEY,
    order_id        UUID           NOT NULL REFERENCES orders (id),
    carrier         VARCHAR(100),
    tracking_number VARCHAR(150),
    status          VARCHAR(50)    NOT NULL,
    shipped_at      TIMESTAMP,
    delivered_at    TIMESTAMP,
    created_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_status_history
(
    id          UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    order_id    UUID           NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    old_status  VARCHAR(50),
    new_status  VARCHAR(50)    NOT NULL,
    changed_by  VARCHAR(255),
    changed_at  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    note        VARCHAR(1000)
);

-- 2) Fan/mobile tables
CREATE TABLE setlists
(
    id           UUID PRIMARY KEY,
    concert_id   UUID         NOT NULL REFERENCES concerts (id) ON DELETE CASCADE,
    title        VARCHAR(255) NOT NULL,
    published_at TIMESTAMP,
    created_by   VARCHAR(255),
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE setlist_items
(
    id               UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    setlist_id       UUID         NOT NULL REFERENCES setlists (id) ON DELETE CASCADE,
    song_title       VARCHAR(255) NOT NULL,
    song_order       INT          NOT NULL,
    duration_seconds INT
);

CREATE TABLE fan_favorites
(
    id            UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    fan_id        VARCHAR(255) NOT NULL,
    favorite_type VARCHAR(30)  NOT NULL,
    reference_id  UUID         NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_fan_favorites UNIQUE (fan_id, favorite_type, reference_id)
);

CREATE TABLE fan_notifications
(
    id           UUID PRIMARY KEY,
    fan_id       VARCHAR(255),
    is_broadcast BOOLEAN      NOT NULL DEFAULT FALSE,
    title        VARCHAR(255) NOT NULL,
    message      VARCHAR(2000) NOT NULL,
    module       VARCHAR(50),
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE fan_notification_reads
(
    id              UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    notification_id UUID         NOT NULL REFERENCES fan_notifications (id) ON DELETE CASCADE,
    fan_id          VARCHAR(255) NOT NULL,
    read_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_fan_notification_reads UNIQUE (notification_id, fan_id)
);

CREATE TABLE fan_devices
(
    id           UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    fan_id       VARCHAR(255) NOT NULL,
    device_token VARCHAR(500) NOT NULL UNIQUE,
    platform     VARCHAR(20)  NOT NULL,
    app_version  VARCHAR(30),
    last_seen_at TIMESTAMP,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3) Indexes
CREATE INDEX idx_payments_order_id ON payments (order_id);
CREATE INDEX idx_payments_status ON payments (status);
CREATE INDEX idx_payment_transactions_payment_id ON payment_transactions (payment_id);
CREATE INDEX idx_shipments_order_id ON shipments (order_id);
CREATE INDEX idx_shipments_status ON shipments (status);
CREATE INDEX idx_order_status_history_order_id ON order_status_history (order_id);
CREATE INDEX idx_setlists_concert_id ON setlists (concert_id);
CREATE INDEX idx_setlist_items_setlist_id ON setlist_items (setlist_id);
CREATE INDEX idx_setlist_items_song_order ON setlist_items (song_order);
CREATE INDEX idx_fan_favorites_fan_id ON fan_favorites (fan_id);
CREATE INDEX idx_fan_notifications_fan_id ON fan_notifications (fan_id);
CREATE INDEX idx_fan_notifications_created_at ON fan_notifications (created_at);
CREATE INDEX idx_fan_notification_reads_fan_id ON fan_notification_reads (fan_id);
CREATE INDEX idx_fan_devices_fan_id ON fan_devices (fan_id);
