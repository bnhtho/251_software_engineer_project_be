-- ===================================================================
-- Migration V21: Thêm dữ liệu mẫu cho bảng hcmut_sso và datacore
-- ===================================================================
-- Mục đích:
--   - Tạo 10 tài khoản sinh viên mẫu để test hệ thống
--   - Mật khẩu đã được mã hóa bằng BCrypt (password gốc: "password123")
--   - Dữ liệu bao gồm sinh viên từ các chuyên ngành khác nhau
--   - Các trạng thái học vụ khác nhau (FRESHMAN -> SENIOR, EXCHANGE)
-- ===================================================================

-- Bước 1: Insert dữ liệu vào bảng hcmut_sso
-- Bảng này chứa thông tin đăng nhập SSO của HCMUT
-- Password mẫu (đã hash): "password123"
INSERT INTO `hcmut_sso` (email, password, hcmut_id) VALUES
('20101234@student.hcmut.edu.vn', '$2a$12$rYvpx3Yiqutf8pIW8AtqeO2jP4Hqtpfl8HnhaYDGNs.CIckpyWl8G', '20101234'),
('20101235@student.hcmut.edu.vn', '$2a$12$rYvpx3Yiqutf8pIW8AtqeO2jP4Hqtpfl8HnhaYDGNs.CIckpyWl8G', '20101235'),
('20101236@student.hcmut.edu.vn', '$2a$12$rYvpx3Yiqutf8pIW8AtqeO2jP4Hqtpfl8HnhaYDGNs.CIckpyWl8G', '20101236'),
('20101237@student.hcmut.edu.vn', '$2a$12$rYvpx3Yiqutf8pIW8AtqeO2jP4Hqtpfl8HnhaYDGNs.CIckpyWl8G', '20101237'),
('20101238@student.hcmut.edu.vn', '$2a$12$rYvpx3Yiqutf8pIW8AtqeO2jP4Hqtpfl8HnhaYDGNs.CIckpyWl8G', '20101238'),
('20101239@student.hcmut.edu.vn', '$2a$12$rYvpx3Yiqutf8pIW8AtqeO2jP4Hqtpfl8HnhaYDGNs.CIckpyWl8G', '20101239'),
('20101240@student.hcmut.edu.vn', '$2a$12$rYvpx3Yiqutf8pIW8AtqeO2jP4Hqtpfl8HnhaYDGNs.CIckpyWl8G', '20101240'),
('20101241@student.hcmut.edu.vn', '$2a$12$rYvpx3Yiqutf8pIW8AtqeO2jP4Hqtpfl8HnhaYDGNs.CIckpyWl8G', '20101241'),
('20101242@student.hcmut.edu.vn', '$2a$12$rYvpx3Yiqutf8pIW8AtqeO2jP4Hqtpfl8HnhaYDGNs.CIckpyWl8G', '20101242'),
('20101243@student.hcmut.edu.vn', '$2a$12$rYvpx3Yiqutf8pIW8AtqeO2jP4Hqtpfl8HnhaYDGNs.CIckpyWl8G', '20101243');

-- Bước 2: Insert dữ liệu vào bảng datacore
-- Bảng này chứa thông tin chi tiết của sinh viên từ hệ thống HCMUT
-- Bao gồm: thông tin cá nhân, chuyên ngành, trạng thái học vụ, liên hệ
INSERT INTO `datacore`
    (hcmut_id, email, major_id, first_name, last_name, profile_image,
     academic_status, dob, phone, other_method_contact)
VALUES
-- Sinh viên 1: Ngành Khoa học máy tính (major_id=1), Năm 1
('20101234', '20101234@student.hcmut.edu.vn', 1, 'An',    'Nguyen',  NULL,
 'FRESHMAN', '2005-03-21', '0901234001', 'Zalo: 0901234001'),

-- Sinh viên 2: Ngành Kỹ thuật phần mềm (major_id=2), Năm 1
('20101235', '20101235@student.hcmut.edu.vn', 2, 'Binh',  'Tran',    NULL,
 'FRESHMAN', '2005-07-10', '0901234002', 'Facebook: fb.com/binh.tran'),

-- Sinh viên 3: Ngành Hệ thống thông tin (major_id=3), Năm 2
('20101236', '20101236@student.hcmut.edu.vn', 3, 'Chau',  'Le',      NULL,
 'SOPHOMORE', '2004-11-02', '0901234003', 'Zalo: 0901234003'),

-- Sinh viên 4: Ngành Khoa học máy tính (major_id=1), Năm 2
('20101237', '20101237@student.hcmut.edu.vn', 1, 'Duc',   'Pham',    NULL,
 'SOPHOMORE', '2004-01-15', '0901234004', 'Telegram: @duc_pham'),

-- Sinh viên 5: Ngành Khoa học dữ liệu (major_id=4), Năm 3
('20101238', '20101238@student.hcmut.edu.vn', 4, 'Ha',    'Vo',      NULL,
 'JUNIOR', '2003-09-30', '0901234005', 'Zalo: 0901234005'),

-- Sinh viên 6: Ngành Kỹ thuật phần mềm (major_id=2), Năm 3
('20101239', '20101239@student.hcmut.edu.vn', 2, 'Khanh', 'Hoang',   NULL,
 'JUNIOR', '2003-05-08', '0901234006', 'Facebook: fb.com/khanh.hoang'),

-- Sinh viên 7: Ngành Hệ thống thông tin (major_id=3), Năm 4
('20101240', '20101240@student.hcmut.edu.vn', 3, 'Linh',  'Pham',    NULL,
 'SENIOR', '2002-12-19', '0901234007', 'Zalo: 0901234007'),

-- Sinh viên 8: Ngành Khoa học máy tính (major_id=1), Năm 4
('20101241', '20101241@student.hcmut.edu.vn', 1, 'Minh',  'Do',      NULL,
 'SENIOR', '2002-08-25', '0901234008', 'Telegram: @minh_do'),

-- Sinh viên 9: Ngành Khoa học dữ liệu (major_id=4), Sinh viên trao đổi
('20101242', '20101242@student.hcmut.edu.vn', 4, 'Nhi',   'Nguyen',  NULL,
 'EXCHANGE', '2004-04-12', '0901234009', 'Zalo: 0901234009'),

-- Sinh viên 10: Ngành Kỹ thuật phần mềm (major_id=2), Năm 1
('20101243', '20101243@student.hcmut.edu.vn', 2, 'Quan',  'Le',      NULL,
 'FRESHMAN', '2006-02-03', '0901234010', 'Facebook: fb.com/quan.le');

-- Bước 3: Thêm constraint kiểm tra giá trị academic_status hợp lệ
-- Đảm bảo chỉ chấp nhận các trạng thái học vụ được định nghĩa
-- FRESHMAN: Năm 1, SOPHOMORE: Năm 2, JUNIOR: Năm 3, SENIOR: Năm 4
-- EXCHANGE: Sinh viên trao đổi, GRADUATED: Đã tốt nghiệp, TEACHER: Giảng viên
ALTER TABLE datacore
  ADD CONSTRAINT chk_datacore_academic_status
    CHECK (academic_status IN (
        'FRESHMAN',
        'SOPHOMORE',
        'JUNIOR',
        'SENIOR',
        'EXCHANGE',
        'GRADUATED',
        'TEACHER'
    ));

