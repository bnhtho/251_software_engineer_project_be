-- V19: Refactor schedule table - add session_id, remove start_time and end_time
-- Date: 2025-11-25

-- QUAN TRỌNG: Xóa tất cả dữ liệu cũ trong bảng schedule
-- Vì structure cũ không có session_id, không thể migrate được
-- Dữ liệu sẽ được tạo lại khi student đăng ký session
DELETE FROM schedule;

-- Bước 1: Thêm cột session_id vào bảng schedule
--  - Kiểu INT UNSIGNED vì session.id cũng là INT UNSIGNED
--  - Cho phép NULL tạm thời
ALTER TABLE schedule
ADD COLUMN session_id INT UNSIGNED NULL;

-- Bước 2: Thêm foreign key từ schedule.session_id -> session.id
--  - ON DELETE CASCADE: nếu xóa 1 session thì các bản ghi schedule liên quan cũng bị xóa theo
ALTER TABLE schedule
ADD CONSTRAINT fk_schedule_session
FOREIGN KEY (session_id) REFERENCES session(id)
ON DELETE CASCADE;

-- Bước 3: Xoá các cột start_time và end_time
--  - Chỉ nên làm sau khi chắc chắn mọi logic đều dùng session_id thay vì start_time/end_time
ALTER TABLE schedule
DROP COLUMN start_time;

ALTER TABLE schedule
DROP COLUMN end_time;

-- Bước 4: Đặt session_id thành NOT NULL
-- Vì đã xóa hết data cũ, giờ có thể set NOT NULL
ALTER TABLE schedule
MODIFY COLUMN session_id INT UNSIGNED NOT NULL;

-- Note:
-- - Dữ liệu schedule sẽ được tạo lại tự động khi:
--   + Student đăng ký session (registerForSession)
--   + Tutor approve student session
-- - Không cần migrate dữ liệu cũ vì structure đã thay đổi hoàn toàn


