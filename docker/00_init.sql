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
CREATE TABLE `library` (
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

-- ============================
-- SAMPLE DATA (MySQL)
-- Thứ tự: role → datacore → hcmut_sso → user → subject → tutor_profile → tutor_schedule → student_schedule → session → notification → reportof_tutor → feedback_student
-- ============================

-- 1) ROLE
INSERT INTO role (id, name, description) VALUES
(1, 'ADMIN',  'System administrator'),
(2, 'TUTOR',  'Tutor / Mentor'),
(3, 'STUDENT','Student');

-- 2) DATACORE (core profile, gán role_id đúng FK)
INSERT INTO datacore
(id, role_id, hcmut_id, email, first_name, last_name, profile_image, faculty, academic_status, dob, phone, other_method_contact)
VALUES
(1, 1, 'NV001',    'loannt@hcmut.edu.vn',  'Nguyễn Thị', 'Loan',  NULL, 'Office of Student Affairs', 'Staff',        '1985-05-12', '0903000001', NULL),
(2, 2, 'GV001',    'minhlv@hcmut.edu.vn',  'Lê Văn',     'Minh',  NULL, 'Applied Science',           'Faculty',      '1978-09-21', '0903000002', NULL),
(3, 2, '17123456', '17123456@hcmut.edu.vn','Phạm Minh',  'Tâm',   NULL, 'CSE',                      'Graduate',     '1997-02-14', '0903000003', NULL),
(4, 3, '20123456', '20123456@hcmut.edu.vn','Nguyễn Văn', 'An',    NULL, 'CSE',                      'Undergraduate','2004-08-10', '0903000004', 'Zalo: an.cse'),
(5, 3, '20125678', '20125678@hcmut.edu.vn','Trần Thị',   'Bình',  NULL, 'Mechanical Engineering',    'Undergraduate','2006-01-18', '0903000005', NULL);

-- 3) HCMUT_SSO (đăng nhập tập trung)
INSERT INTO hcmut_sso (id, email, password, hcmut_id) VALUES
(1, 'loannt@hcmut.edu.vn',   '$2a$12$rYvpx3Yiqutf8pIW8AtqeO2jP4Hqtpfl8HnhaYDGNs.CIckpyWl8G',   'NV001'),
(2, '17123456@hcmut.edu.vn', '$2a$12$rYvpx3Yiqutf8pIW8AtqeO2jP4Hqtpfl8HnhaYDGNs.CIckpyWl8G',   '17123456'),
(3, '20123456@hcmut.edu.vn', '$2a$12$rYvpx3Yiqutf8pIW8AtqeO2jP4Hqtpfl8HnhaYDGNs.CIckpyWl8G', '20123456');

-- 4) USER (tài khoản hệ thống ứng dụng; dùng chuỗi role + hcmut_id để liên hệ)
INSERT INTO `user`
(id, status, created_date, update_date, last_login, role, hcmut_id, first_name, last_name, profile_image, faculty, academic_status, dob, phone, other_method_contact)
VALUES
(1, 'ACTIVE',  NOW(), NULL, NULL, 'ADMIN',  'NV001',    'Nguyễn Thị', 'Loan', NULL, 'Office of Student Affairs', 'Staff',        '1985-05-12', '0903000001', NULL),
(2, 'ACTIVE',  NOW(), NULL, NULL, 'TUTOR',  'GV001',    'Lê Văn',     'Minh', NULL, 'Applied Science',            'Faculty',      '1978-09-21', '0903000002', NULL),
(3, 'ACTIVE',  NOW(), NULL, NULL, 'TUTOR',  '17123456', 'Phạm Minh',  'Tâm',  NULL, 'CSE',                        'Graduate',     '1997-02-14', '0903000003', NULL),
(4, 'ACTIVE',  NOW(), NULL, NULL, 'STUDENT','20123456', 'Nguyễn Văn', 'An',   NULL, 'CSE',                        'Undergraduate','2004-08-10', '0903000004', 'Zalo: an.cse'),
(5, 'ACTIVE',  NOW(), NULL, NULL, 'STUDENT','20125678', 'Trần Thị',   'Bình', NULL, 'Mechanical Engineering',     'Undergraduate','2006-01-18', '0903000005', NULL);

-- 5) SUBJECT
INSERT INTO `subject`
(id, code, name, faculty, description, libary_resources)
VALUES
(1, 'MATH101', 'Calculus I',                  'Applied Science', 'Giải tích cơ bản (đạo hàm, tích phân).', NULL),
(2, 'CS101',   'Programming Fundamentals',    'CSE',             'Lập trình cơ bản C/C++.',                 NULL),
(3, 'PHYS101', 'Physics I',                   'Physics',         'Cơ học, nhiệt học cơ bản.',              NULL);

-- 6) Học liệu (bảng tên `library`)
INSERT INTO `library`
(id, `name`, catagory, author, `subject`, url, uploaded_date, uploaded_by)
VALUES
(1, 'Calculus I - Lecture Notes', 'Notes',     'Dept. of Math', 'MATH101', 'https://library.hcmut.edu.vn/calc1.pdf',  NOW(), 2),
(2, 'Prog 101 - Textbook',       'Textbook',  'CS Dept',      'CS101',   'https://library.hcmut.edu.vn/prog101.pdf', NOW(), 3);

-- 7) TUTOR_PROFILE (hồ sơ tutor, FK: user_id → user.id)
INSERT INTO tutor_profile
(id, user_id, `subject`, experience_years, bio, rating, priority, total_sessions_completed, is_available)
VALUES
(1, 2, 'MATH101;PHYS101', 10, 'GV Toán cao cấp, kinh nghiệm 10 năm.', 4.85, 1, 120, 1),
(2, 3, 'CS101',            3,  'NCS CNTT, dạy C/C++ & CTDL.',          4.70, 2, 45,  1);

-- 8) TUTOR_SCHEDULE (0=Sun..6=Sat; TIME)
INSERT INTO tutor_schedule
(id, tutor_id, day_of_week, start_time, end_time, status, created_date, update_date)
VALUES
(1, 2, 2, '09:00:00', '11:00:00', 'OPEN', NOW(), NULL), -- Tue
(2, 2, 4, '14:00:00', '16:00:00', 'OPEN', NOW(), NULL), -- Thu
(3, 3, 3, '14:00:00', '15:00:00', 'OPEN', NOW(), NULL), -- Wed
(4, 3, 6, '09:00:00', '11:00:00', 'OPEN', NOW(), NULL); -- Sat

-- 9) STUDENT_SCHEDULE (availability của student)
INSERT INTO student_schedule
(id, student_id, day_of_week, start_time, end_time, status, created_date, update_date)
VALUES
(1, 4, 2, '08:00:00', '12:00:00', 'FREE', NOW(), NULL), -- An: Tue sáng
(2, 4, 4, '13:00:00', '17:00:00', 'FREE', NOW(), NULL), -- An: Thu chiều
(3, 5, 2, '08:00:00', '12:00:00', 'FREE', NOW(), NULL), -- Bình: Tue sáng
(4, 5, 3, '13:00:00', '17:00:00', 'FREE', NOW(), NULL); -- Bình: Wed chiều

-- 10) SESSION (mỗi buổi gán 1 tutor_id & 1 student_id)
-- Ngày hiện tại: 2025-11-25
-- COMPLETED: thời gian ở quá khứ
-- SCHEDULED: thời gian ở tương lai
-- Khoảng cách start_time và end_time: 1-2 giờ (không quá 24h)
INSERT INTO `session`
(id, tutor_id, student_id, subject_id, start_time, end_time, `format`, location, status, created_date, updated_date)
VALUES
-- Session 1: COMPLETED - đã diễn ra 1 tuần trước (2025-11-18)
(1, 2, 4, 1, '2025-11-18 09:00:00', '2025-11-18 10:30:00', 'offline', 'B10-201', 'COMPLETED', '2025-11-15 10:00:00', '2025-11-18 10:30:00'),

-- Session 2: SCHEDULED - sẽ diễn ra 2 ngày sau (2025-11-27)
(2, 3, 4, 2, '2025-11-27 14:00:00', '2025-11-27 15:30:00', 'online',  'Zoom',    'SCHEDULED', '2025-11-20 14:00:00', '2025-11-20 14:00:00'),

-- Session 3: SCHEDULED - sẽ diễn ra 5 ngày sau (2025-11-30)
(3, 3, 5, 2, '2025-11-30 14:00:00', '2025-11-30 16:00:00', 'offline', 'B6-204',  'SCHEDULED', '2025-11-22 09:00:00', '2025-11-22 09:00:00');

-- 11) NOTIFICATION
INSERT INTO notification
(id, user_id, related_session_id, type, title, message, is_read, sent_at, created_date)
VALUES
(1, 4, 1, 'REMIND', 'Nhắc lịch buổi học', 'Bạn có buổi Calculus I lúc 09:00 18/11/2025 tại B10-201.', 1, '2025-11-17 18:00:00', '2025-11-17 18:00:00'),
(2, 4, 2, 'REMIND', 'Nhắc lịch buổi học', 'Bạn có buổi CS101 lúc 14:00 27/11/2025 trên Zoom.', 0, '2025-11-26 18:00:00', '2025-11-25 10:00:00'),
(3, 5, 3, 'REMIND', 'Nhắc lịch buổi học', 'Bạn có buổi CS101 lúc 14:00 30/11/2025 tại B6-204.', 0, '2025-11-29 18:00:00', '2025-11-25 10:00:00');

-- 12) REPORTOF_TUTOR (báo cáo sau buổi)
INSERT INTO reportof_tutor
(id, session_id, tutor_comment, student_summary, student_performance, material_used)
VALUES
(1, 1, 'SV An nắm vững đạo hàm cơ bản, cần luyện bài ứng dụng.', 'Tham gia tích cực, hỏi bài tốt.', 'Tốt', 'Calculus I - Lecture Notes'),
(2, 2, 'Đã ôn mảng & vòng lặp, làm bài tập mẫu tốt.',             'Hiểu nhanh, hoàn thành bài.',    'Tốt', 'Prog 101 - Textbook'),
(3, 3, 'Cần chậm lại phần cú pháp C++ cho SV Bình.',               'Tiếp thu được phần cơ bản.',      'Trung bình', 'Prog 101 - Textbook');

-- 13) FEEDBACK_STUDENT (đánh giá của SV)
-- Chỉ có feedback cho session đã COMPLETED
INSERT INTO feedback_student
(id, session_id, student_id, overall_rating, content_quality, teaching_effectiveness, communication, comment, suggestion, would_recommend, created_date)
VALUES
(1, 1, 4, 5, 5, 5, 5, 'Buổi học rất rõ ràng, em tự tin hơn.', 'Cho thêm bài tập ứng dụng.', 1, '2025-11-18 12:00:00');
