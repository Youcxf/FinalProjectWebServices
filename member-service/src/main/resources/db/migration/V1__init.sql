CREATE TABLE members (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(32) NOT NULL,
    outstanding_fees DECIMAL(10,2) NOT NULL
);

INSERT INTO members (id, name, email, status, outstanding_fees) VALUES
('11111111-1111-1111-1111-111111111111', 'Alice Reader', 'alice@example.com', 'ACTIVE', 0.00),
('22222222-2222-2222-2222-222222222222', 'Bob Overdue', 'bob@example.com', 'SUSPENDED', 12.50);
