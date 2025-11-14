CREATE TABLE `status` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,         -- tên hiển thị (ACTIVE, INACTIVE...)
  `description` VARCHAR(255) NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_status_name` (`name`)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
INSERT INTO `status` (`name`, `description`) VALUES
('ACTIVE', 'Tài khoản/đối tượng đang hoạt động và có thể sử dụng bình thường.'),
('INACTIVE', 'Tài khoản/đối tượng đã bị vô hiệu hóa hoặc ngừng hoạt động.');