-- Create the exchange_rates table to store currency exchange rates
CREATE TABLE exchange_rates (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    base_currency VARCHAR(3) NOT NULL,
    target_currency VARCHAR(3) NOT NULL,
    rate DECIMAL(19, 6) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_exchange_rates_date ON exchange_rates(date);
CREATE INDEX idx_exchange_rates_target_currency ON exchange_rates(target_currency);
CREATE INDEX idx_exchange_rates_date_currencies ON exchange_rates(date, target_currency);

-- Constraints
ALTER TABLE exchange_rates ADD CONSTRAINT chk_rate_positive CHECK (rate > 0);
