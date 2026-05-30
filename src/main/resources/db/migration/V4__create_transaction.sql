CREATE TABLE transaction (
id UUID PRIMARY KEY,
merchant_id UUID NOT NULL REFERENCES merchant(id),
amount_cents INTEGER NOT NULL,
status varchar(30) NOT NULL,
created_at timestamptz NOT NULL
);