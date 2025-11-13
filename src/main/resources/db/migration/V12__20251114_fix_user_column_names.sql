-- Fix user table column names to match JPA naming strategy (CamelCaseToUnderscoresNamingStrategy)
-- Handle both lowercase and camelCase versions (PostgreSQL is case-sensitive when quoted)

-- Rename phoneNumber/phonenumber to phone_number
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'user' AND column_name = 'phoneNumber') THEN
        ALTER TABLE "user" RENAME COLUMN "phoneNumber" TO phone_number;
    ELSIF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'user' AND column_name = 'phonenumber') THEN
        ALTER TABLE "user" RENAME COLUMN phonenumber TO phone_number;
    END IF;
END $$;

-- Rename firstName/firstname to first_name
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'user' AND column_name = 'firstName') THEN
        ALTER TABLE "user" RENAME COLUMN "firstName" TO first_name;
    ELSIF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'user' AND column_name = 'firstname') THEN
        ALTER TABLE "user" RENAME COLUMN firstname TO first_name;
    END IF;
END $$;

-- Rename lastName/lastname to last_name
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'user' AND column_name = 'lastName') THEN
        ALTER TABLE "user" RENAME COLUMN "lastName" TO last_name;
    ELSIF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'user' AND column_name = 'lastname') THEN
        ALTER TABLE "user" RENAME COLUMN lastname TO last_name;
    END IF;
END $$;
