-- Insert default admin user
-- Default credentials: username=admin, password=admin123
-- BCrypt hash for 'admin123'
INSERT INTO "user" (
    username,
    phone_number,
    email,
    password,
    first_name,
    last_name,
    address,
    role_type,
    max_book_number,
    max_borrow_duration,
    active,
    membership_date,
    created_at,
    updated_at
) VALUES (
    'admin',
    '0000000000',
    'admin@library.com',
    '$2a$10$N.zmdr9k7uOCQvOPiuqRg.GIbdZG8N6OGQDwS4cWtWqrS5QCxqCn6', -- BCrypt hash of 'admin123'
    'System',
    'Administrator',
    'System',
    'ADMIN',
    20,
    60,
    true,
    CURRENT_DATE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (username) DO NOTHING; -- Avoid duplicate if already exists
