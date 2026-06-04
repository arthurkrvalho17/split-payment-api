CREATE TABLE payment_event (
id UUID NOT NULL PRIMARY KEY,
transaction_id UUID NOT NULL REFERENCES transaction(id),
event_type varchar(50) NOT NULL,
payload JSONB NOT NULL,
created_at TIMESTAMPTZ NOT NULL
);