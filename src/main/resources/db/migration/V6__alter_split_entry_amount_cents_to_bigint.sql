ALTER TABLE split_entry
    ALTER COLUMN amount_cents TYPE BIGINT;

ALTER TABLE transaction
    ALTER COLUMN amount_cents TYPE BIGINT;