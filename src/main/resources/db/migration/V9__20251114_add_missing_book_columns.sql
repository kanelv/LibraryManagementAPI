-- Add missing columns to book table to match JPA entity

-- Drop old column
ALTER TABLE book DROP COLUMN IF EXISTS publication_date;

-- Add new columns
ALTER TABLE book ADD COLUMN IF NOT EXISTS publisher VARCHAR(100);
ALTER TABLE book ADD COLUMN IF NOT EXISTS published_year INTEGER;
ALTER TABLE book ADD COLUMN IF NOT EXISTS genre VARCHAR(50);
