-- Docker
INSERT INTO student_session
  (status_id, student_id, session_id, status, registered_date, confirmed_date, updated_date)
VALUES 
  -- 1. Đăng ký mới, chưa confirm
  (1, 4, 1, 'pending',    '2025-11-24 09:15:00', NULL,                         NULL),

  -- 2. Đăng ký đã confirm
  (2, 5, 1, 'confirmed',  '2025-11-24 10:00:00', '2025-11-24 10:05:00',        '2025-11-24 10:05:00');