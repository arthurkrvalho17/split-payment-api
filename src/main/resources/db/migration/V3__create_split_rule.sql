CREATE TABLE split_rule (
id UUID PRIMARY KEY,
merchant_id UUID NOT NULL REFERENCES merchant(id),
recipient_id UUID NOT NULL REFERENCES recipient(id),
percent int NOT NULL,
type varchar(20) NOT NULL,
created_at timestamptz NOT NULL
);