-- Add email column to the user table
ALTER TABLE "user"
    ADD COLUMN email VARCHAR(50) NOT NULL UNIQUE;

-- Create an index on the email column
CREATE INDEX idx_email ON "user"(email);
