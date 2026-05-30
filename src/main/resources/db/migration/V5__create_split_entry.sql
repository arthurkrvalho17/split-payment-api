CREATE TABLE split_entry (
id UUID NOT NULL PRIMARY KEY,
transaction_id UUID NOT NULL REFERENCES transaction(id),
recipient_id UUID NOT NULL REFERENCES recipient(id),
amount_cents INTEGER NOT NULL,
percent_applied INTEGER NOT NULL,
type varchar(20) NOT NULL,
created_at timestamptz NOT NULL
);