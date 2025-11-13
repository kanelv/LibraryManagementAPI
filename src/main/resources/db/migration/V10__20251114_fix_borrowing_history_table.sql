-- Fix borrowing_history table to match JPA Borrowing entity
-- Note: member_id was already renamed to user_id in V5

-- Drop old column and add new columns
ALTER TABLE borrowing_history DROP COLUMN IF EXISTS borrow_from;

ALTER TABLE borrowing_history ADD COLUMN IF NOT EXISTS borrow_date DATE NOT NULL DEFAULT CURRENT_DATE;
ALTER TABLE borrowing_history ADD COLUMN IF NOT EXISTS due_date DATE NOT NULL DEFAULT (CURRENT_DATE + INTERVAL '14 days');
ALTER TABLE borrowing_history ADD COLUMN IF NOT EXISTS return_date DATE;
ALTER TABLE borrowing_history ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'BORROWED';
