CREATE TABLE tour_revenues
(
    id           UUID PRIMARY KEY,
    tour_id       UUID REFERENCES tours (id),
    title         VARCHAR(255)   NOT NULL,
    amount        NUMERIC(19, 2) NOT NULL,
    currency      VARCHAR(3) DEFAULT 'PLN',
    revenue_date  TIMESTAMP
);
