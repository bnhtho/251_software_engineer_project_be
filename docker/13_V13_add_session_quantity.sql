-- V13: Thêm max_quantity và current_quantity vào session table
-- max_quantity: Số lượng tối đa sinh viên có thể được approved, bắt buộc phải nhập bởi tutor
-- current_quantity: Số lượng sinh viên hiện tại đã được approved

-- Thêm max_quantity (số lượng tối đa sinh viên)
-- Lúc thêm mới, set giá trị mặc định là 50 cho các record hiện có
-- Sau này tutor phải nhập giá trị này khi tạo session mới
ALTER TABLE `session`
ADD COLUMN `max_quantity` INT NOT NULL DEFAULT 50 COMMENT 'Số lượng tối đa sinh viên có thể đăng ký (bắt buộc nhập)' AFTER `location`;

-- Thêm current_quantity (số lượng hiện tại đã được approve)
ALTER TABLE `session`
ADD COLUMN `current_quantity` INT NOT NULL DEFAULT 0 COMMENT 'Số lượng sinh viên hiện tại đã được approved' AFTER `max_quantity`;

-- Thêm constraint để đảm bảo current_quantity không vượt quá max_quantity
ALTER TABLE `session`
ADD CONSTRAINT `chk_session_quantity` CHECK (`current_quantity` <= `max_quantity`);

-- Tạo index để tối ưu query
CREATE INDEX idx_session_quantity ON `session`(`current_quantity`, `max_quantity`);

