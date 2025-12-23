-- V1__Init_Schema.sql

-- 1. MODUŁ E-COMMERCE
CREATE TABLE product_categories
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE products
(
    id             UUID PRIMARY KEY,
    name           VARCHAR(255)   NOT NULL,
    description    VARCHAR(2000),
    price          NUMERIC(19, 2) NOT NULL, -- Typ dla pieniędzy
    currency       VARCHAR(3) DEFAULT 'PLN',
    stock_quantity INT            NOT NULL,
    image_url      VARCHAR(500),
    category_id    UUID REFERENCES product_categories (id)
);

CREATE TABLE orders
(
    id           UUID PRIMARY KEY,
    user_id      VARCHAR(255) NOT NULL, -- ID z Keycloak (String)
    order_date   TIMESTAMP    NOT NULL,
    status       VARCHAR(50)  NOT NULL,
    total_amount NUMERIC(19, 2)
);

CREATE TABLE order_items
(
    id         UUID PRIMARY KEY,
    order_id   UUID REFERENCES orders (id),
    product_id UUID REFERENCES products (id),
    quantity   INT            NOT NULL,
    unit_price NUMERIC(19, 2) NOT NULL
);

-- 2. MODUŁ TICKETING
CREATE TABLE venues
(
    id       UUID PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    city     VARCHAR(100) NOT NULL,
    address  VARCHAR(255),
    capacity INT
);

CREATE TABLE concerts
(
    id        UUID PRIMARY KEY,
    name      VARCHAR(255) NOT NULL,
    date      TIMESTAMP    NOT NULL,
    venue_id  UUID REFERENCES venues (id),
    tour_name VARCHAR(255)
);

CREATE TABLE ticket_pools
(
    id                 UUID PRIMARY KEY,
    concert_id         UUID REFERENCES concerts (id),
    name               VARCHAR(100)   NOT NULL, -- np. "Bilet Normalny"
    price              NUMERIC(19, 2) NOT NULL,
    currency           VARCHAR(3) DEFAULT 'PLN',
    total_quantity     INT            NOT NULL,
    remaining_quantity INT            NOT NULL
);

CREATE TABLE tickets
(
    id            UUID PRIMARY KEY,
    ticket_code   VARCHAR(100) NOT NULL UNIQUE, -- Unikalny kod np. QR
    pool_id       UUID REFERENCES ticket_pools (id),
    user_id       VARCHAR(255),                 -- ID Fana
    purchase_date TIMESTAMP
);

-- 3. MODUŁ CMS
CREATE TABLE news_articles
(
    id             UUID PRIMARY KEY,
    title          VARCHAR(255) NOT NULL,
    content        TEXT         NOT NULL,
    published_date TIMESTAMP,
    author_id      VARCHAR(255)
);
