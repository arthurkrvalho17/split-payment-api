CREATE TABLE recipient (
id UUID PRIMARY KEY,
name varchar(100) NOT NULL,
document varchar(14) NOT NULL,
bank_account varchar(20) NOT NULL,
created_at timestamptz NOT NULL
);