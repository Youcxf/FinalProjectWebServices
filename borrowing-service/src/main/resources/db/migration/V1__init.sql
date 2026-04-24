CREATE TABLE loans (
    id UUID PRIMARY KEY,
    member_id UUID NOT NULL,
    book_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    start_date DATE NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(32) NOT NULL
);

INSERT INTO loans (id, member_id, book_id, quantity, start_date, due_date, status) VALUES
('55555555-5555-5555-5555-555555555551', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333331', 1, '2026-04-01', '2026-04-15', 'ACTIVE');
