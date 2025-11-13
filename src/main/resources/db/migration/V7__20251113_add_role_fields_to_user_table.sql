-- Add_role_fields_to_user_table.sql
ALTER TABLE "user"
    ADD COLUMN role_type VARCHAR(20) NOT NULL DEFAULT 'USER',
    ADD COLUMN max_book_number INTEGER DEFAULT 5,
    ADD COLUMN max_borrow_duration INTEGER DEFAULT 14;

-- Add check constraints
ALTER TABLE "user"
    ADD CONSTRAINT chk_role_type CHECK (role_type IN ('ADMIN', 'USER', 'GUEST'));
