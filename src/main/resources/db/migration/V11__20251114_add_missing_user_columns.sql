-- Add missing columns to user table to match JPA User entity

ALTER TABLE "user" ADD COLUMN IF NOT EXISTS active BOOLEAN NOT NULL DEFAULT true;
ALTER TABLE "user" ADD COLUMN IF NOT EXISTS membership_date DATE NOT NULL DEFAULT CURRENT_DATE;
