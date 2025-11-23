-- V14: Thêm confirmed_date vào student_session table
-- Cột này lưu thời điểm student được tutor confirm

ALTER TABLE `student_session`
ADD COLUMN `confirmed_date` TIMESTAMP NULL COMMENT 'Thời điểm được tutor confirm, NULL nếu chưa confirm' AFTER `registered_date`;

