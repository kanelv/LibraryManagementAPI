-- Rename columns in book table to match JPA entity mapping
ALTER TABLE book RENAME COLUMN total TO total_copies;
ALTER TABLE book RENAME COLUMN stock TO available_copies;
