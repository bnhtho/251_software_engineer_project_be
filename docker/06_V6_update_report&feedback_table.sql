SET FOREIGN_KEY_CHECKS = 0;

-- =========================
-- 1) feedback_student (MVP)
-- =========================
ALTER TABLE `feedback_student`
  DROP COLUMN `content_quality`,
  DROP COLUMN `teaching_effectiveness`,
  DROP COLUMN `suggestion`,
  CHANGE COLUMN `overall_rating` `rating` DECIMAL(2,1) NOT NULL,
  MODIFY `comment` TEXT NULL,
  MODIFY `would_recommend` BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE `feedback_student`
  ADD CONSTRAINT `chk_fb_rating`
    CHECK (rating BETWEEN 0.5 AND 5.0 AND MOD(rating*10,5) = 0);

ALTER TABLE `feedback_student`
  ADD CONSTRAINT `uq_feedback_student_session`
  UNIQUE (`session_id`, `student_id`);

-- (tuỳ chọn) FK nếu chưa có
-- ALTER TABLE `feedback_student`
--   ADD CONSTRAINT `fk_fb_session` FOREIGN KEY (`session_id`) REFERENCES `session`(`id`)
--     ON UPDATE CASCADE ON DELETE CASCADE,
--   ADD CONSTRAINT `fk_fb_student` FOREIGN KEY (`student_id`) REFERENCES `user`(`id`)
--     ON UPDATE CASCADE ON DELETE CASCADE;

-- ======================
-- 2) reportof_tutor MVP
-- ======================
ALTER TABLE `reportof_tutor`
  DROP COLUMN `student_summary`,
  DROP COLUMN `material_used`,
  DROP COLUMN `student_performance`,
  MODIFY `tutor_comment` TEXT NULL;

ALTER TABLE `reportof_tutor`
	ADD `student_performance` INT UNSIGNED NULL;
ALTER TABLE `reportof_tutor`
  ADD CONSTRAINT `chk_report_perf`
    CHECK (`student_performance` BETWEEN 1 AND 10);

ALTER TABLE `reportof_tutor`
  ADD COLUMN `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `student_performance`,
  ADD COLUMN `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                           ON UPDATE CURRENT_TIMESTAMP AFTER `created_at`;

ALTER TABLE `reportof_tutor`
  ADD CONSTRAINT `uq_report_session` UNIQUE (`session_id`);

-- (tuỳ chọn) FK nếu chưa có
-- ALTER TABLE `reportof_tutor`
--   ADD CONSTRAINT `fk_report_session` FOREIGN KEY (`session_id`) REFERENCES `session`(`id`)
--     ON UPDATE CASCADE ON DELETE CASCADE;

-- ==========================================
-- 3) Bảng tách tài liệu: report_material
-- ==========================================
CREATE TABLE `report_material` (
  `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `report_id`  BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_report_material_report` (`report_id`),
  CONSTRAINT `fk_report_material_report`
    FOREIGN KEY (`report_id`) REFERENCES `reportof_tutor`(`id`)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
-- nối với library + ngày dùng
ALTER TABLE `report_material`
  ADD COLUMN `library_id` BIGINT UNSIGNED NULL AFTER `report_id`,
  ADD COLUMN `used_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ADD KEY `idx_report_material_library` (`library_id`),
  ADD CONSTRAINT `fk_report_material_library`
    FOREIGN KEY (`library_id`) REFERENCES `library`(`id`)
    ON UPDATE CASCADE ON DELETE SET NULL;
SET FOREIGN_KEY_CHECKS = 1;