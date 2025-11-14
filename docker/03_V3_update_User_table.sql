-- 0) Đảm bảo có status id=1 (ACTIVE)
INSERT IGNORE INTO `status` (`id`, `name`, `description`)
VALUES (1, 'ACTIVE', 'Trạng thái hoạt động');

-- 1) Xóa cột VARCHAR status (nếu đang tồn tại)
ALTER TABLE `user`
  DROP COLUMN `status`;

-- 2) Thêm cột status_id, mặc định = 1, NOT NULL
ALTER TABLE `user`
  ADD COLUMN `status_id` BIGINT UNSIGNED NOT NULL DEFAULT 1 AFTER `id`;

-- 3) (khuyến nghị) tạo chỉ mục + khóa ngoại
ALTER TABLE `user`
  ADD KEY `idx_user_status_id` (`status_id`),
  ADD CONSTRAINT `fk_user_status`
    FOREIGN KEY (`status_id`) REFERENCES `status`(`id`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;
