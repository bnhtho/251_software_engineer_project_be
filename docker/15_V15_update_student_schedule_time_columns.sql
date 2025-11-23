-- V15: Update student_schedule time columns to DATETIME
-- Đổi start_time và end_time từ TIME sang DATETIME để lưu thời gian cụ thể (ngày + giờ)
-- Lý do: Cần so sánh thời gian chính xác với session để check trùng lịch

-- Xóa dữ liệu cũ nếu có (vì không thể convert trực tiếp từ TIME sang DATETIME)
TRUNCATE TABLE `student_schedule`;

-- Đổi start_time từ TIME sang DATETIME
ALTER TABLE `student_schedule`
MODIFY COLUMN `start_time` DATETIME NOT NULL COMMENT 'Thời gian bắt đầu (ngày + giờ)';

-- Đổi end_time từ TIME sang DATETIME
ALTER TABLE `student_schedule`
MODIFY COLUMN `end_time` DATETIME NOT NULL COMMENT 'Thời gian kết thúc (ngày + giờ)';

-- Thêm index để tối ưu query check trùng lịch
CREATE INDEX idx_student_schedule_time ON `student_schedule`(`student_id`, `day_of_week`, `start_time`, `end_time`);

