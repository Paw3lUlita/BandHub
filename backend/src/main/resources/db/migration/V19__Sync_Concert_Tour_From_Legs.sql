-- Powiązanie koncert ↔ trasa: zsynchronizuj istniejące dane i popraw agregację przychodu z biletów.

-- 1) Backfill: koncerty przypięte do odcinka trasy dostają concerts.tour_id
UPDATE concerts c
SET tour_id = tl.tour_id
FROM tour_legs tl
WHERE tl.concert_id = c.id
  AND c.tour_id IS DISTINCT FROM tl.tour_id;

-- 2) Widok rentowności — bilety z concerts.tour_id LUB tour_legs.concert_id (bez podwójnego liczenia)
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
         LEFT JOIN (SELECT x.tour_id, SUM(to2.total_amount) AS ticket_revenue
                    FROM (SELECT c.tour_id AS tour_id, c.id AS concert_id
                          FROM concerts c
                          WHERE c.tour_id IS NOT NULL
                          UNION
                          SELECT tl.tour_id, tl.concert_id
                          FROM tour_legs tl
                          WHERE tl.concert_id IS NOT NULL) x
                             JOIN ticket_orders to2 ON to2.concert_id = x.concert_id
                    WHERE COALESCE(UPPER(TRIM(to2.status)), '') <> 'CANCELLED'
                    GROUP BY x.tour_id) ticketing ON ticketing.tour_id = t.id;

-- 3) Rozliczenie trasy — ta sama logika co w panelu admina
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
    FROM ticket_orders to2
    WHERE COALESCE(UPPER(TRIM(to2.status)), '') <> 'CANCELLED'
      AND to2.concert_id IN (SELECT c.id
                             FROM concerts c
                             WHERE c.tour_id = p_tour_id
                             UNION
                             SELECT tl.concert_id
                             FROM tour_legs tl
                             WHERE tl.tour_id = p_tour_id
                               AND tl.concert_id IS NOT NULL);

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
