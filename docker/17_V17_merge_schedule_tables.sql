DROP TABLE tutor_schedule CASCADE;
ALTER TABLE student_schedule RENAME TO schedule;
ALTER TABLE schedule
RENAME COLUMN student_id TO user_id;
ALTER TABLE schedule
MODIFY COLUMN user_id INT UNSIGNED NOT NULL;
ALTER TABLE schedule
    MODIFY day_of_week TINYINT NOT NULL;

ALTER TABLE schedule
    ADD CONSTRAINT chk_schedule_day_of_week
    CHECK (day_of_week BETWEEN 0 AND 6);
INSERT INTO schedule (
    user_id,
    day_of_week,
    start_time,
    end_time,
    created_date,
    update_date
)
VALUES
(2, 2, '2000-01-01 09:00:00', '2000-01-01 11:00:00', NOW(), NULL), -- Tue
(2, 4, '2000-01-01 14:00:00', '2000-01-01 16:00:00', NOW(), NULL), -- Thu
(3, 3, '2000-01-01 14:00:00', '2000-01-01 15:00:00', NOW(), NULL), -- Wed
(3, 6, '2000-01-01 09:00:00', '2000-01-01 11:00:00', NOW(), NULL); -- Sat
