-- PHẦN 1: KHỞI TẠO DATABASE VÀ CẤU HÌNH
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET collation_connection = 'utf8mb4_unicode_ci';

CREATE DATABASE IF NOT EXISTS tutor_system
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE tutor_system;

-- =========================================================
-- 1. CSHT HCMUT KHỐI ROLE / DATACORE / SSO / TÀI LIỆU
-- =========================================================

CREATE TABLE `role` (
  `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name`         VARCHAR(50)     NOT NULL,
  `description`  VARCHAR(255)    NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `hcmut_sso` (
  `id`        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `email`     VARCHAR(255)    NOT NULL,
  `password`  VARCHAR(255)    NOT NULL,
  `hcmut_id`  VARCHAR(50)     NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_hcmut_sso_email` (`email`),
  UNIQUE KEY `uk_hcmut_sso_hcmut_id` (`hcmut_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `datacore` (
  `id`                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `role_id`             BIGINT UNSIGNED NULL,
  `hcmut_id`            VARCHAR(50)     NOT NULL,
  `email`               VARCHAR(255)    NOT NULL,
  `first_name`          VARCHAR(100)    NULL,
  `last_name`           VARCHAR(100)    NULL,
  `profile_image`       VARCHAR(255)    NULL,
  `faculty`             VARCHAR(100)    NULL,
  `academic_status`     VARCHAR(100)    NULL,
  `dob`                 DATE            NULL,
  `phone`               VARCHAR(20)     NULL,
  `other_method_contact` VARCHAR(255)   NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_datacore_hcmut_id` (`hcmut_id`),
  CONSTRAINT `fk_datacore_role`
    FOREIGN KEY (`role_id`) REFERENCES `role`(`id`)
      ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng tài liệu (tên là "Table" theo diagram, dùng backtick để tránh trùng keyword)
CREATE TABLE `Table` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name`          VARCHAR(255)    NOT NULL,
  `catagory`      VARCHAR(100)    NULL,  -- (category)
  `author`        VARCHAR(255)    NULL,
  `subject`       VARCHAR(255)    NULL,
  `url`           VARCHAR(255)    NULL,
  `uploaded_date` DATETIME        NULL,
  `uploaded_by`   BIGINT UNSIGNED NULL,  -- có thể liên kết tới user/datacore nếu muốn
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================================
-- 2. KHỐI USER / SUBJECT / SESSION / PROFILE / SCHEDULE / NOTI
-- =========================================================

CREATE TABLE `user` (
  `id`                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `status`             VARCHAR(20)     NULL,
  `created_date`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_date`        DATETIME        NULL,
  `last_login`         DATETIME        NULL,
  `role`               VARCHAR(50)     NULL,  -- (student/tutor/admin...), có thể tách FK sang bảng role nếu muốn
  `hcmut_id`           VARCHAR(50)     NULL,
  `first_name`         VARCHAR(100)    NULL,
  `last_name`          VARCHAR(100)    NULL,
  `profile_image`      VARCHAR(255)    NULL,
  `faculty`            VARCHAR(100)    NULL,
  `academic_status`    VARCHAR(100)    NULL,
  `dob`                DATE            NULL,
  `phone`              VARCHAR(20)     NULL,
  `other_method_contact` VARCHAR(255)  NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_hcmut_id` (`hcmut_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `subject` (
  `id`               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `code`             VARCHAR(50)     NOT NULL,
  `name`             VARCHAR(255)    NOT NULL,
  `faculty`          VARCHAR(100)    NULL,
  `description`      TEXT            NULL,
  `libary_resources` TEXT            NULL,  -- (library_resources)
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_subject_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `session` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `tutor_id`    BIGINT UNSIGNED NOT NULL,
  `student_id`  BIGINT UNSIGNED NOT NULL,
  `subject_id`  BIGINT UNSIGNED NOT NULL,
  `start_time`  DATETIME        NOT NULL,
  `end_time`    DATETIME        NOT NULL,
  `format`      VARCHAR(50)     NULL,       -- online/offline/...
  `location`    VARCHAR(255)    NULL,
  `status`      VARCHAR(50)     NULL,
  `created_date` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` DATETIME       NULL,
  PRIMARY KEY (`id`),
  KEY `idx_session_tutor` (`tutor_id`),
  KEY `idx_session_student` (`student_id`),
  KEY `idx_session_subject` (`subject_id`),
  CONSTRAINT `fk_session_tutor`
    FOREIGN KEY (`tutor_id`) REFERENCES `user`(`id`)
      ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_session_student`
    FOREIGN KEY (`student_id`) REFERENCES `user`(`id`)
      ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_session_subject`
    FOREIGN KEY (`subject_id`) REFERENCES `subject`(`id`)
      ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `tutor_profile` (
  `id`                     BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id`                BIGINT UNSIGNED NOT NULL,
  `subject`                VARCHAR(255)    NULL,  -- có thể lưu list subject code
  `experience_years`       TINYINT UNSIGNED NULL,
  `bio`                    TEXT            NULL,
  `rating`                 DECIMAL(3,2)    NULL,  -- 0.00–5.00
  `priority`               INT UNSIGNED    NULL,
  `total_sessions_completed` INT UNSIGNED  NOT NULL DEFAULT 0,
  `is_available`           TINYINT(1)      NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tutor_profile_user` (`user_id`),
  CONSTRAINT `fk_tutor_profile_user`
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
      ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `tutor_schedule` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `tutor_id`    BIGINT UNSIGNED NOT NULL,
  `day_of_week` TINYINT UNSIGNED NOT NULL,  -- 0=Sun..6=Sat
  `start_time`  TIME            NOT NULL,
  `end_time`    TIME            NOT NULL,
  `status`      VARCHAR(50)     NULL,
  `created_date` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_date` DATETIME        NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tutor_schedule_tutor` (`tutor_id`),
  CONSTRAINT `fk_tutor_schedule_tutor`
    FOREIGN KEY (`tutor_id`) REFERENCES `user`(`id`)
      ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- student_schedule trong hình chưa thiết kế chi tiết,
-- ở đây em giả sử cấu trúc đối xứng với tutor_schedule
CREATE TABLE `student_schedule` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `student_id`  BIGINT UNSIGNED NOT NULL,
  `day_of_week` TINYINT UNSIGNED NOT NULL,
  `start_time`  TIME            NOT NULL,
  `end_time`    TIME            NOT NULL,
  `status`      VARCHAR(50)     NULL,
  `created_date` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_date` DATETIME        NULL,
  PRIMARY KEY (`id`),
  KEY `idx_student_schedule_student` (`student_id`),
  CONSTRAINT `fk_student_schedule_student`
    FOREIGN KEY (`student_id`) REFERENCES `user`(`id`)
      ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `notification` (
  `id`                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id`           BIGINT UNSIGNED NOT NULL,
  `related_session_id` BIGINT UNSIGNED NULL,
  `type`              VARCHAR(50)     NULL,
  `title`             VARCHAR(255)    NULL,
  `message`           TEXT            NULL,
  `is_read`           TINYINT(1)      NOT NULL DEFAULT 0,
  `sent_at`           DATETIME        NULL,
  `created_date`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_notification_user` (`user_id`),
  KEY `idx_notification_session` (`related_session_id`),
  CONSTRAINT `fk_notification_user`
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `fk_notification_session`
    FOREIGN KEY (`related_session_id`) REFERENCES `session`(`id`)
      ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================================
-- 3. REPORT VÀ FEEDBACK
-- =========================================================

CREATE TABLE `reportof_tutor` (
  `id`                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `session_id`          BIGINT UNSIGNED NOT NULL,
  `tutor_comment`       TEXT            NULL,
  `student_summary`     TEXT            NULL,
  `student_performance` TEXT            NULL,
  `material_used`       TEXT            NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_reportof_tutor_session` (`session_id`),
  CONSTRAINT `fk_reportof_tutor_session`
    FOREIGN KEY (`session_id`) REFERENCES `session`(`id`)
      ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `feedback_student` (
  `id`                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `session_id`          BIGINT UNSIGNED NOT NULL,
  `student_id`          BIGINT UNSIGNED NOT NULL,
  `overall_rating`      TINYINT UNSIGNED NULL,
  `content_quality`     TINYINT UNSIGNED NULL,
  `teaching_effectiveness` TINYINT UNSIGNED NULL,
  `communication`       TINYINT UNSIGNED NULL,
  `comment`             TEXT            NULL,
  `suggestion`          TEXT            NULL,
  `would_recommend`     TINYINT(1)      NULL,
  `created_date`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_feedback_session` (`session_id`),
  KEY `idx_feedback_student` (`student_id`),
  CONSTRAINT `fk_feedback_session`
    FOREIGN KEY (`session_id`) REFERENCES `session`(`id`)
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `fk_feedback_student_user`
    FOREIGN KEY (`student_id`) REFERENCES `user`(`id`)
      ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;