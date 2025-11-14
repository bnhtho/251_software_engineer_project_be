SET FOREIGN_KEY_CHECKS = 0;

-- 1) Tạo bảng phụ N–N
CREATE TABLE `tutor_profile_subject` (
  `tutor_profile_id` BIGINT UNSIGNED NOT NULL,
  `subject_id`       BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`tutor_profile_id`, `subject_id`),
  KEY `idx_tps_subject` (`subject_id`),
  CONSTRAINT `fk_tps_tutor`
    FOREIGN KEY (`tutor_profile_id`) REFERENCES `tutor_profile`(`id`)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `fk_tps_subject`
    FOREIGN KEY (`subject_id`) REFERENCES `subject`(`id`)
    ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
