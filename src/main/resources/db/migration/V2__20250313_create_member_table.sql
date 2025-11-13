DROP TABLE IF EXISTS member CASCADE;
DROP SEQUENCE IF EXISTS member_seq;

CREATE SEQUENCE member_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE member (
    id BIGINT PRIMARY KEY DEFAULT NEXTVAL('member_seq'),
    username VARCHAR(100) NOT NULL UNIQUE,
    phoneNumber VARCHAR(15) NOT NULL,
    password VARCHAR(255) NOT NULL,
    firstName VARCHAR(50),
    lastName VARCHAR(50),
    address VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uc_username_phoneNumber UNIQUE (username, phoneNumber)
);

CREATE INDEX idx_username ON member(username);
CREATE INDEX idx_phoneNumber ON member(phoneNumber);
