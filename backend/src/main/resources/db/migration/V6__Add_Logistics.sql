-- V6__Add_Logistics.sql

CREATE TABLE tours
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    start_date  TIMESTAMP,
    end_date    TIMESTAMP
);

CREATE TABLE tour_costs
(
    id          UUID PRIMARY KEY,
    tour_id     UUID REFERENCES tours (id),
    title       VARCHAR(255)   NOT NULL,
    amount      NUMERIC(19, 2) NOT NULL,
    currency    VARCHAR(3) DEFAULT 'PLN',
    cost_date   TIMESTAMP
);

ALTER TABLE concerts
    ADD COLUMN tour_id UUID REFERENCES tours (id);

ALTER TABLE concerts
    DROP COLUMN tour_name;