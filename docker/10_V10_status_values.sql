-- 1) ĐỔI TÊN BẢNG STATUS → USER_STATUS (giữ dữ liệu)
RENAME TABLE `status` TO `user_status`;

-- Đảm bảo UNIQUE cho name (nếu chưa có)
ALTER TABLE `user_status`
  ADD UNIQUE KEY uq_user_status_name (`name`);

-- 2) TẠO BẢNG SESSION_STATUS
CREATE TABLE IF NOT EXISTS `session_status` (
  `id`          TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name`        VARCHAR(32) NOT NULL,
  `description` VARCHAR(255) NULL,
  `created_at`  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_session_status_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed tối thiểu
INSERT INTO `session_status` (`id`,`name`,`description`) VALUES
(1,'SCHEDULED','Đã lên lịch'),
(2,'IN_PROGRESS','Đang diễn ra'),
(3,'COMPLETED','Hoàn thành'),
(4,'CANCELLED','Đã hủy')
ON DUPLICATE KEY UPDATE `name`=VALUES(`name`);

-- 3) TẠO BẢNG STUDENT_SESSION_STATUS
CREATE TABLE IF NOT EXISTS `student_session_status` (
  `id`          TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name`        VARCHAR(32) NOT NULL,
  `description` VARCHAR(255) NULL,
  `created_at`  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_student_session_status_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Seed tối thiểu
INSERT INTO `student_session_status` (`id`,`name`,`description`) VALUES
(1,'PENDING','Chờ tutor xác nhận'),
(2,'CONFIRMED','Đã xác nhận'),
(3,'CANCELLED','Đã hủy'),
(4,'REJECTED','Bị từ chối')
ON DUPLICATE KEY UPDATE `name`=VALUES(`name`);

-- 4) THÊM KHÓA NGOẠI CHO USER, SESSION, STUDENT_SESSIO

-- 4.1) SESSION → SESSION_STATUS
-- (đổi tên bảng nếu bạn đang dùng `tutor_session` hay `class_session`)
ALTER TABLE `session`
  ADD COLUMN `status_id` TINYINT UNSIGNED NOT NULL DEFAULT 1 AFTER `id`,
  ADD CONSTRAINT `fk_session_status`
    FOREIGN KEY (`status_id`) REFERENCES `session_status`(`id`);

-- 4.3) STUDENT_SESSION → STUDENT_SESSION_STATUS
ALTER TABLE `student_session`
  ADD COLUMN `status_id` TINYINT UNSIGNED NOT NULL DEFAULT 1 AFTER `id`,
  ADD CONSTRAINT `fk_student_session_status`
    FOREIGN KEY (`status_id`) REFERENCES `student_session_status`(`id`);
