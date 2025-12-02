ALTER TABLE tutor_profile
	DROP COLUMN subject,
	DROP COLUMN is_available;

-- 2) Đổi tên bảng cho "generic" hơn, dùng chung cho
--    cả đăng ký buổi học và đăng ký trở thành tutor
RENAME TABLE student_session_status TO registration_status;
ALTER TABLE registration_status
    CONVERT TO CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

-- 3) (Tuỳ chọn) Đổi mô tả để không nhắc riêng tới tutor,
--    dùng chung cho mọi loại đăng ký / đơn xét duyệt
UPDATE registration_status
SET description = 'Đang chờ xử lý'
WHERE name = 'PENDING';

UPDATE registration_status
SET description = 'Đã được chấp nhận'
WHERE name = 'CONFIRMED';

UPDATE registration_status
SET description = 'Đã bị huỷ'
WHERE name = 'CANCELLED';

UPDATE registration_status
SET description = 'Đã bị từ chối'
WHERE name = 'REJECTED';

ALTER TABLE registration_status
    ADD CONSTRAINT uq_registration_status_name UNIQUE (name);

-- 1) Thêm cột status_id tạm thời (NULL để còn map dữ liệu)
ALTER TABLE tutor_profile
    ADD COLUMN status_id TINYINT UNSIGNED NULL AFTER status;

ALTER TABLE tutor_profile
    CONVERT TO CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

-- 2) Copy dữ liệu từ status (text) sang status_id (id từ registration_status)
UPDATE tutor_profile tp
JOIN registration_status rs
      ON rs.name = tp.status
SET tp.status_id = rs.id;

-- (Tuỳ chọn) kiểm tra xem còn bản ghi nào chưa map được không
-- SELECT * FROM tutor_profile WHERE status_id IS NULL;

-- 3) Xoá cột status cũ, ép NOT NULL cho status_id và tạo foreign key
ALTER TABLE tutor_profile
    MODIFY COLUMN status_id TINYINT UNSIGNED NOT NULL,
    DROP COLUMN status,
    ADD CONSTRAINT fk_tutor_profile_status
        FOREIGN KEY (status_id) REFERENCES registration_status(id);
ALTER TABLE tutor_profile
    ALTER COLUMN status_id SET DEFAULT 1;

ALTER TABLE student_session
    DROP FOREIGN KEY fk_student_session_status;

-- 2) Tạo FK mới trỏ đúng bảng registration_status
ALTER TABLE student_session
    ADD CONSTRAINT fk_student_session_status
        FOREIGN KEY (status_id) REFERENCES registration_status(id);
-- ----------------------------------
-- 1) Thêm cột role_id vào bảng user (tạm thời cho phép NULL để migrate dữ liệu)
ALTER TABLE user
    ADD COLUMN role_id INT UNSIGNED NULL AFTER role;

-- 2) Copy dữ liệu role_id từ bảng datacore sang bảng user
--    join qua hcmut_id (giống pattern em đã dùng cho major_id)
UPDATE user u
JOIN datacore d ON u.hcmut_id = d.hcmut_id
SET u.role_id = d.role_id;

-- (tuỳ chọn) kiểm tra còn user nào chưa có role_id sau khi copy
-- SELECT * FROM user WHERE role_id IS NULL;

-- 3) Ép NOT NULL cho role_id, xoá cột role (string) cũ, thêm foreign key sang bảng role
ALTER TABLE user
    MODIFY COLUMN role_id INT UNSIGNED NOT NULL,
    DROP COLUMN role,
    ADD CONSTRAINT fk_user_role
        FOREIGN KEY (role_id) REFERENCES role(id);

-- 4) (Nếu datacore.role_id đang có foreign key, phải drop FK trước khi drop cột)
--    Thay 'fk_datacore_role' bằng tên constraint thật trong DB của em.
ALTER TABLE datacore
    DROP FOREIGN KEY fk_datacore_role;

-- 5) Xoá cột role_id ở bảng datacore
ALTER TABLE datacore
    DROP COLUMN role_id CASCADE;

