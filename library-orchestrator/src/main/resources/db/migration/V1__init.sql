CREATE TABLE borrowing_decisions (
    id UUID PRIMARY KEY,
    member_id UUID NOT NULL,
    book_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    start_date DATE NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(32) NOT NULL,
    decision_reason VARCHAR(255) NOT NULL,
    loan_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

INSERT INTO borrowing_decisions (
    id, member_id, book_id, quantity, start_date, due_date, status, decision_reason, loan_id, created_at, updated_at
) VALUES (
    '77777777-7777-7777-7777-777777777771',
    '11111111-1111-1111-1111-111111111111',
    '33333333-3333-3333-3333-333333333331',
    1,
    '2026-04-01',
    '2026-04-15',
    'APPROVED',
    'Initial migrated sample decision.',
    '55555555-5555-5555-5555-555555555551',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
