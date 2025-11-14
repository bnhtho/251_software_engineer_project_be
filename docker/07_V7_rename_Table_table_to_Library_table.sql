-- Đổi 'catagory' -> 'category', bỏ cột text 'subject', thêm FK tới subject(id)
ALTER TABLE `library`
  CHANGE COLUMN `catagory` `category` VARCHAR(100) NULL,
  DROP COLUMN `subject`,
  ADD COLUMN `subject_id` BIGINT UNSIGNED NULL AFTER `category`,
  ADD KEY `idx_library_subject` (`subject_id`),
  ADD CONSTRAINT `fk_library_subject`
    FOREIGN KEY (`subject_id`) REFERENCES `subject`(`id`)
    ON UPDATE CASCADE ON DELETE SET NULL;
