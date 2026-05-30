CREATE TABLE merchant (
id UUID PRIMARY KEY,
name VARCHAR(100) NOT NULL,
document VARCHAR(14) NOT NULL,
created_at timestamptz NOT NULL
);