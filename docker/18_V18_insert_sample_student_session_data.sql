-- V18: Thêm dữ liệu mẫu cho bảng student_session
-- ========== STUDENT ID = 4 ==========

-- Student 4 - Session 1 đã được confirm (CONFIRMED - status_id = 2)
INSERT INTO `student_session`
(`student_id`, `session_id`, `status_id`, `registered_date`, `confirmed_date`, `updated_date`)
VALUES
(4, 1, 2, '2025-11-01 10:00:00', '2025-11-01 15:30:00', '2025-11-01 15:30:00')
ON DUPLICATE KEY UPDATE
    status_id       = VALUES(status_id),
    registered_date = VALUES(registered_date),
    confirmed_date  = VALUES(confirmed_date),
    updated_date    = VALUES(updated_date);

-- Student 4 - Session 2 đang chờ duyệt (PENDING - status_id = 1)
INSERT INTO `student_session`
(`student_id`, `session_id`, `status_id`, `registered_date`, `confirmed_date`, `updated_date`)
VALUES
(4, 2, 1, '2025-11-10 14:20:00', NULL, NULL)
ON DUPLICATE KEY UPDATE
    status_id       = VALUES(status_id),
    registered_date = VALUES(registered_date),
    confirmed_date  = VALUES(confirmed_date),
    updated_date    = VALUES(updated_date);

-- Student 4 - Session 3 đã được confirm (CONFIRMED - status_id = 2)
INSERT INTO `student_session`
(`student_id`, `session_id`, `status_id`, `registered_date`, `confirmed_date`, `updated_date`)
VALUES
(4, 3, 2, '2025-11-05 09:15:00', '2025-11-05 18:45:00', '2025-11-05 18:45:00')
ON DUPLICATE KEY UPDATE
    status_id       = VALUES(status_id),
    registered_date = VALUES(registered_date),
    confirmed_date  = VALUES(confirmed_date),
    updated_date    = VALUES(updated_date);

-- ========== STUDENT ID = 5 ==========

-- Student 5 - Session 1 đang chờ duyệt (PENDING - status_id = 1)
INSERT INTO `student_session`
(`student_id`, `session_id`, `status_id`, `registered_date`, `confirmed_date`, `updated_date`)
VALUES
(5, 1, 1, '2025-11-02 11:00:00', NULL, NULL)
ON DUPLICATE KEY UPDATE
    status_id       = VALUES(status_id),
    registered_date = VALUES(registered_date),
    confirmed_date  = VALUES(confirmed_date),
    updated_date    = VALUES(updated_date);

-- Student 5 - Session 2 đã được confirm (CONFIRMED - status_id = 2)
INSERT INTO `student_session`
(`student_id`, `session_id`, `status_id`, `registered_date`, `confirmed_date`, `updated_date`)
VALUES
(5, 2, 2, '2025-11-12 13:45:00', '2025-11-12 18:00:00', '2025-11-12 18:00:00')
ON DUPLICATE KEY UPDATE
    status_id       = VALUES(status_id),
    registered_date = VALUES(registered_date),
    confirmed_date  = VALUES(confirmed_date),
    updated_date    = VALUES(updated_date);

-- Student 5 - Session 3 bị từ chối (REJECTED - status_id = 4)
INSERT INTO `student_session`
(`student_id`, `session_id`, `status_id`, `registered_date`, `confirmed_date`, `updated_date`)
VALUES
(5, 3, 4, '2025-11-15 10:30:00', NULL, '2025-11-15 14:20:00')
ON DUPLICATE KEY UPDATE
    status_id       = VALUES(status_id),
    registered_date = VALUES(registered_date),
    confirmed_date  = VALUES(confirmed_date),
    updated_date    = VALUES(updated_date);
