-- Sprint 14: optional category + tour leg on costs/revenues

ALTER TABLE tour_costs
    ADD COLUMN cost_category_id UUID REFERENCES tour_cost_categories (id),
    ADD COLUMN tour_leg_id UUID REFERENCES tour_legs (id);

ALTER TABLE tour_revenues
    ADD COLUMN revenue_category_id UUID REFERENCES tour_revenue_categories (id),
    ADD COLUMN tour_leg_id UUID REFERENCES tour_legs (id);

CREATE INDEX idx_tour_costs_cost_category_id ON tour_costs (cost_category_id);
CREATE INDEX idx_tour_costs_tour_leg_id ON tour_costs (tour_leg_id);
CREATE INDEX idx_tour_revenues_revenue_category_id ON tour_revenues (revenue_category_id);
CREATE INDEX idx_tour_revenues_tour_leg_id ON tour_revenues (tour_leg_id);
