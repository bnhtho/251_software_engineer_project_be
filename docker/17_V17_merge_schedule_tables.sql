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

-- Không insert dữ liệu ở đây vì cấu trúc bảng sẽ thay đổi ở V19
-- (thêm session_id, xóa start_time/end_time)
-- Dữ liệu sẽ được tạo tự động khi student đăng ký session
