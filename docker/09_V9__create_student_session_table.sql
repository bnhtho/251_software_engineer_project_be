-- V9: Create StudentSession table for N-N relationship between Student and Session
-- This replaces the direct student_id foreign key in session table

CREATE TABLE student_session (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT UNSIGNED NOT NULL,
    session_id BIGINT UNSIGNED NOT NULL,
    status VARCHAR(50) DEFAULT 'pending' COMMENT 'pending, confirmed, cancelled, rejected',
    registered_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_date TIMESTAMP NULL,

    CONSTRAINT fk_student_session_student FOREIGN KEY (student_id) REFERENCES user(id) ON DELETE CASCADE,
    CONSTRAINT fk_student_session_session FOREIGN KEY (session_id) REFERENCES session(id) ON DELETE CASCADE,

    -- Prevent duplicate registrations
    CONSTRAINT uk_student_session UNIQUE (student_id, session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Remove student_id from session table (if it exists)
-- Note: This will break existing data, migration needed
ALTER TABLE session DROP FOREIGN KEY fk_session_student;
ALTER TABLE session DROP COLUMN student_id;


