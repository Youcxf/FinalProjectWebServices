CREATE TABLE books (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL UNIQUE,
    author VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    available_copies INTEGER NOT NULL
);

INSERT INTO books (id, title, author, status, available_copies) VALUES
('33333333-3333-3333-3333-333333333331', 'Domain-Driven Design Distilled', 'Vaughn Vernon', 'AVAILABLE', 3),
('33333333-3333-3333-3333-333333333332', 'Clean Architecture', 'Robert C. Martin', 'AVAILABLE', 2),
('33333333-3333-3333-3333-333333333333', 'Building Microservices', 'Sam Newman', 'RESERVED', 1);
