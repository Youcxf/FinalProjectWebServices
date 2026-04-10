CREATE TABLE members (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(32) NOT NULL,
    outstanding_fees DECIMAL(10,2) NOT NULL
);

CREATE TABLE books (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    available_copies INTEGER NOT NULL
);

CREATE TABLE fees (
    id UUID PRIMARY KEY,
    member_id UUID NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(32) NOT NULL
);

CREATE TABLE borrowing_transactions (
    id UUID PRIMARY KEY,
    member_id UUID NOT NULL,
    status VARCHAR(32) NOT NULL,
    start_date DATE NOT NULL,
    due_date DATE NOT NULL
);

CREATE TABLE borrowed_items (
    id UUID PRIMARY KEY,
    borrowing_transaction_id UUID NOT NULL,
    book_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    CONSTRAINT fk_borrowed_item_borrowing
        FOREIGN KEY (borrowing_transaction_id) REFERENCES borrowing_transactions(id) ON DELETE CASCADE
);

CREATE TABLE return_transactions (
    id UUID PRIMARY KEY,
    borrowing_transaction_id UUID NOT NULL UNIQUE,
    return_date DATE NOT NULL,
    return_status VARCHAR(32) NOT NULL
);

INSERT INTO members (id, name, email, status, outstanding_fees) VALUES
('11111111-1111-1111-1111-111111111111', 'Alice Reader', 'alice@example.com', 'ACTIVE', 0.00),
('22222222-2222-2222-2222-222222222222', 'Bob Overdue', 'bob@example.com', 'SUSPENDED', 12.50);

INSERT INTO books (id, title, author, status, available_copies) VALUES
('33333333-3333-3333-3333-333333333331', 'Domain-Driven Design Distilled', 'Vaughn Vernon', 'AVAILABLE', 3),
('33333333-3333-3333-3333-333333333332', 'Clean Architecture', 'Robert C. Martin', 'AVAILABLE', 2),
('33333333-3333-3333-3333-333333333333', 'Building Microservices', 'Sam Newman', 'RESERVED', 1);

INSERT INTO fees (id, member_id, amount, status) VALUES
('44444444-4444-4444-4444-444444444441', '11111111-1111-1111-1111-111111111111', 0.00, 'PAID'),
('44444444-4444-4444-4444-444444444442', '22222222-2222-2222-2222-222222222222', 12.50, 'UNPAID');

INSERT INTO borrowing_transactions (id, member_id, status, start_date, due_date) VALUES
('55555555-5555-5555-5555-555555555551', '11111111-1111-1111-1111-111111111111', 'ACTIVE', '2026-04-01', '2026-04-15');

INSERT INTO borrowed_items (id, borrowing_transaction_id, book_id, quantity) VALUES
('66666666-6666-6666-6666-666666666661', '55555555-5555-5555-5555-555555555551', '33333333-3333-3333-3333-333333333331', 1);
