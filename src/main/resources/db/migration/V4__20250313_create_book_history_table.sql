DROP TABLE IF EXISTS borrowing_history CASCADE;
DROP SEQUENCE IF EXISTS borrowing_history_seq;

CREATE SEQUENCE borrowing_history_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE borrowing_history (
    id BIGINT PRIMARY KEY DEFAULT NEXTVAL('borrowing_history_seq'),
    member_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    borrow_from TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Ensure borrow_from is always present
    penalty NUMERIC(10,2) DEFAULT 0.00,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_borrowing_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    CONSTRAINT fk_borrowing_book FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE
);

CREATE INDEX idx_borrowing_member ON borrowing_history(member_id);
CREATE INDEX idx_borrowing_book ON borrowing_history(book_id);
