-- Sprint 9 (lean): logistics detail + reporting objects + SQL views/functions

-- 1) Logistics detail tables
CREATE TABLE tour_legs
(
    id             UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    tour_id         UUID           NOT NULL REFERENCES tours (id) ON DELETE CASCADE,
    concert_id      UUID REFERENCES concerts (id),
    leg_order       INT            NOT NULL,
    city            VARCHAR(100)   NOT NULL,
    venue_name      VARCHAR(255),
    leg_date        TIMESTAMP,
    planned_budget  NUMERIC(19, 2),
    currency        VARCHAR(3) DEFAULT 'PLN'
);

CREATE TABLE tour_cost_categories
(
    id      UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    code    VARCHAR(50)  NOT NULL UNIQUE,
    name    VARCHAR(100) NOT NULL,
    active  BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE tour_revenue_categories
(
    id      UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    code    VARCHAR(50)  NOT NULL UNIQUE,
    name    VARCHAR(100) NOT NULL,
    active  BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE tour_settlements
(
    id            UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    tour_id       UUID           NOT NULL UNIQUE REFERENCES tours (id) ON DELETE CASCADE,
    settled_by    VARCHAR(255),
    settled_at    TIMESTAMP,
    total_costs   NUMERIC(19, 2) NOT NULL DEFAULT 0,
    total_revenue NUMERIC(19, 2) NOT NULL DEFAULT 0,
    balance       NUMERIC(19, 2) NOT NULL DEFAULT 0,
    currency      VARCHAR(3) DEFAULT 'PLN',
    notes         VARCHAR(1000)
);

-- 2) Reporting/system tables
CREATE TABLE report_runs
(
    id              UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    report_name     VARCHAR(100)  NOT NULL,
    requested_by    VARCHAR(255),
    parameters_json TEXT,
    status          VARCHAR(30)   NOT NULL,
    file_format     VARCHAR(20),
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at    TIMESTAMP
);

CREATE TABLE export_jobs
(
    id           UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    module       VARCHAR(50)  NOT NULL,
    entity_name  VARCHAR(50)  NOT NULL,
    status       VARCHAR(30)  NOT NULL,
    requested_by VARCHAR(255),
    file_path    VARCHAR(500),
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);

-- 3) Indexes
CREATE INDEX idx_tour_legs_tour_id ON tour_legs (tour_id);
CREATE INDEX idx_tour_legs_concert_id ON tour_legs (concert_id);
CREATE INDEX idx_tour_legs_leg_date ON tour_legs (leg_date);
CREATE INDEX idx_tour_settlements_tour_id ON tour_settlements (tour_id);
CREATE INDEX idx_report_runs_name_status ON report_runs (report_name, status);
CREATE INDEX idx_report_runs_created_at ON report_runs (created_at);
CREATE INDEX idx_export_jobs_module_status ON export_jobs (module, status);
CREATE INDEX idx_export_jobs_created_at ON export_jobs (created_at);

-- 4) Views
CREATE OR REPLACE VIEW vw_tour_profitability AS
SELECT t.id                                                 AS tour_id,
       t.name                                               AS tour_name,
       COALESCE(costs.total_costs, 0)                       AS total_costs,
       COALESCE(revenues.manual_revenue, 0)                 AS manual_revenue,
       COALESCE(ticketing.ticket_revenue, 0)                AS ticket_revenue,
       COALESCE(revenues.manual_revenue, 0) +
       COALESCE(ticketing.ticket_revenue, 0)                AS total_revenue,
       (COALESCE(revenues.manual_revenue, 0) +
        COALESCE(ticketing.ticket_revenue, 0)) -
       COALESCE(costs.total_costs, 0)                       AS balance
FROM tours t
         LEFT JOIN (SELECT tc.tour_id, SUM(tc.amount) AS total_costs
                    FROM tour_costs tc
                    GROUP BY tc.tour_id) costs ON costs.tour_id = t.id
         LEFT JOIN (SELECT tr.tour_id, SUM(tr.amount) AS manual_revenue
                    FROM tour_revenues tr
                    GROUP BY tr.tour_id) revenues ON revenues.tour_id = t.id
         LEFT JOIN (SELECT c.tour_id, SUM(to2.total_amount) AS ticket_revenue
                    FROM concerts c
                             JOIN ticket_orders to2 ON to2.concert_id = c.id
                    GROUP BY c.tour_id) ticketing ON ticketing.tour_id = t.id;

CREATE OR REPLACE VIEW vw_top_concerts AS
SELECT c.id                                  AS concert_id,
       c.name                                AS concert_name,
       COUNT(DISTINCT to2.id)                AS orders_count,
       COALESCE(SUM(to2.total_amount), 0)    AS revenue
FROM concerts c
         LEFT JOIN ticket_orders to2 ON to2.concert_id = c.id
GROUP BY c.id, c.name
ORDER BY revenue DESC;

CREATE OR REPLACE VIEW vw_fan_activity AS
WITH fan_base AS (SELECT o.user_id AS fan_id
                  FROM orders o
                  UNION
                  SELECT to2.user_id AS fan_id
                  FROM ticket_orders to2
                  UNION
                  SELECT ff.fan_id
                  FROM fan_favorites ff
                  UNION
                  SELECT fnr.fan_id
                  FROM fan_notification_reads fnr)
SELECT fb.fan_id,
       COALESCE(om.orders_count, 0)          AS merch_orders_count,
       COALESCE(om.orders_total, 0)          AS merch_orders_total,
       COALESCE(ot.ticket_orders_count, 0)   AS ticket_orders_count,
       COALESCE(ot.ticket_orders_total, 0)   AS ticket_orders_total,
       COALESCE(ffv.favorites_count, 0)      AS favorites_count,
       COALESCE(fr.read_notifications, 0)    AS read_notifications
FROM fan_base fb
         LEFT JOIN (SELECT o.user_id, COUNT(*) AS orders_count, SUM(o.total_amount) AS orders_total
                    FROM orders o
                    GROUP BY o.user_id) om ON om.user_id = fb.fan_id
         LEFT JOIN (SELECT to2.user_id,
                           COUNT(*) AS ticket_orders_count,
                           SUM(to2.total_amount) AS ticket_orders_total
                    FROM ticket_orders to2
                    GROUP BY to2.user_id) ot ON ot.user_id = fb.fan_id
         LEFT JOIN (SELECT ff.fan_id, COUNT(*) AS favorites_count
                    FROM fan_favorites ff
                    GROUP BY ff.fan_id) ffv ON ffv.fan_id = fb.fan_id
         LEFT JOIN (SELECT fnr.fan_id, COUNT(*) AS read_notifications
                    FROM fan_notification_reads fnr
                    GROUP BY fnr.fan_id) fr ON fr.fan_id = fb.fan_id;

-- 5) SQL functions
CREATE OR REPLACE FUNCTION fn_close_tour_settlement(p_tour_id UUID, p_settled_by VARCHAR)
    RETURNS VOID
    LANGUAGE plpgsql
AS
$$
DECLARE
    v_total_costs   NUMERIC(19, 2);
    v_manual_rev    NUMERIC(19, 2);
    v_ticket_rev    NUMERIC(19, 2);
    v_total_rev     NUMERIC(19, 2);
    v_balance       NUMERIC(19, 2);
BEGIN
    SELECT COALESCE(SUM(tc.amount), 0)
    INTO v_total_costs
    FROM tour_costs tc
    WHERE tc.tour_id = p_tour_id;

    SELECT COALESCE(SUM(tr.amount), 0)
    INTO v_manual_rev
    FROM tour_revenues tr
    WHERE tr.tour_id = p_tour_id;

    SELECT COALESCE(SUM(to2.total_amount), 0)
    INTO v_ticket_rev
    FROM concerts c
             JOIN ticket_orders to2 ON to2.concert_id = c.id
    WHERE c.tour_id = p_tour_id;

    v_total_rev := COALESCE(v_manual_rev, 0) + COALESCE(v_ticket_rev, 0);
    v_balance := v_total_rev - COALESCE(v_total_costs, 0);

    INSERT INTO tour_settlements (tour_id, settled_by, settled_at, total_costs, total_revenue, balance, currency)
    VALUES (p_tour_id, p_settled_by, CURRENT_TIMESTAMP, v_total_costs, v_total_rev, v_balance, 'PLN')
    ON CONFLICT (tour_id) DO UPDATE
        SET settled_by    = EXCLUDED.settled_by,
            settled_at    = EXCLUDED.settled_at,
            total_costs   = EXCLUDED.total_costs,
            total_revenue = EXCLUDED.total_revenue,
            balance       = EXCLUDED.balance;
END;
$$;

CREATE OR REPLACE FUNCTION fn_fan_activity_summary(p_fan_id VARCHAR)
    RETURNS TABLE
            (
                fan_id                 VARCHAR,
                merch_orders_count     BIGINT,
                merch_orders_total     NUMERIC,
                ticket_orders_count    BIGINT,
                ticket_orders_total    NUMERIC,
                favorites_count        BIGINT,
                read_notifications     BIGINT
            )
    LANGUAGE sql
AS
$$
SELECT p_fan_id,
       (SELECT COUNT(*) FROM orders o WHERE o.user_id = p_fan_id),
       COALESCE((SELECT SUM(o.total_amount) FROM orders o WHERE o.user_id = p_fan_id), 0),
       (SELECT COUNT(*) FROM ticket_orders to2 WHERE to2.user_id = p_fan_id),
       COALESCE((SELECT SUM(to2.total_amount) FROM ticket_orders to2 WHERE to2.user_id = p_fan_id), 0),
       (SELECT COUNT(*) FROM fan_favorites ff WHERE ff.fan_id = p_fan_id),
       (SELECT COUNT(*) FROM fan_notification_reads fnr WHERE fnr.fan_id = p_fan_id);
$$;
