-- 1) DEPARTMENT (Khoa/Trung tâm)
CREATE TABLE `department` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_department_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2) MAJOR (mỗi dòng = 1 chương trình thuộc 1 khoa)
CREATE TABLE `major` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `department_id` BIGINT UNSIGNED NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `major_code` CHAR(7) NOT NULL,
  `program_code` CHAR(3) NOT NULL,
  `note` VARCHAR(255) NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_major_department` (`department_id`),
  UNIQUE KEY `uk_major_combo` (`major_code`,`program_code`), -- combo duy nhất
  CONSTRAINT `fk_major_department`
    FOREIGN KEY (`department_id`) REFERENCES `department`(`id`)
    ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO `department` (`name`) VALUES
('Khoa Cơ khí'),
('Khoa Điện - Điện tử'),
('Khoa Kỹ thuật Xây dựng'),
('Khoa Kỹ thuật Hóa học'),
('Khoa Khoa học và Kỹ thuật Máy tính'),
('Khoa Kỹ thuật Địa chất và Dầu khí'),
('Khoa Khoa học Ứng dụng'),
('Khoa Công nghệ Vật liệu'),
('Khoa Kỹ thuật Giao thông'),
('Khoa Quản lý Công nghiệp'),
('Khoa Môi trường và Tài nguyên'),
('Trung tâm Đào tạo Bảo dưỡng Công nghiệp');

-- ------------------------------------------------------------------------------
-- KHOA KHOA HỌC VÀ KỸ THUẬT MÁY TÍNH
-- ------------------------------------------------------------------------------
INSERT INTO major (department_id, name, major_code, program_code, note) VALUES
((SELECT id FROM department WHERE name='Khoa Khoa học và Kỹ thuật Máy tính'), 'Khoa học Máy tính',                '7480101','106', NULL),
((SELECT id FROM department WHERE name='Khoa Khoa học và Kỹ thuật Máy tính'), 'Kỹ thuật Máy tính',                '7480106','107', NULL),
-- Chương trình CLC/tiếng Anh/tiếng Nhật
((SELECT id FROM department WHERE name='Khoa Khoa học và Kỹ thuật Máy tính'), 'Khoa học Máy tính (Chất lượng cao)','7480101','206','CLC'),
((SELECT id FROM department WHERE name='Khoa Khoa học và Kỹ thuật Máy tính'), 'Kỹ thuật Máy tính (Chất lượng cao)','7480106','207','CLC'),
((SELECT id FROM department WHERE name='Khoa Khoa học và Kỹ thuật Máy tính'), 'Khoa học Máy tính (CLC tăng cường tiếng Nhật)','7480101','266','CLC JP');

-- ------------------------------------------------------------------------------
-- KHOA ĐIỆN - ĐIỆN TỬ
-- ------------------------------------------------------------------------------
INSERT INTO major (department_id, name, major_code, program_code, note) VALUES
((SELECT id FROM department WHERE name='Khoa Điện - Điện tử'), 'Kỹ thuật Điện',                         '7520201','108', NULL),
((SELECT id FROM department WHERE name='Khoa Điện - Điện tử'), 'Kỹ thuật Điện tử - Viễn thông',         '7520207','108', NULL),
((SELECT id FROM department WHERE name='Khoa Điện - Điện tử'), 'Kỹ thuật Điều khiển và Tự động hóa',    '7520216','108', NULL),
-- Chương trình tiên tiến
((SELECT id FROM department WHERE name='Khoa Điện - Điện tử'), 'Kỹ sư Điện - Điện tử (Chương trình Tiên tiến)', '7520201','208','Tiên tiến (TA)');

-- ------------------------------------------------------------------------------
-- KHOA CƠ KHÍ
-- ------------------------------------------------------------------------------
INSERT INTO major (department_id, name, major_code, program_code, note) VALUES
((SELECT id FROM department WHERE name='Khoa Cơ khí'), 'Kỹ thuật Cơ khí',                         '7520103','109', NULL),
((SELECT id FROM department WHERE name='Khoa Cơ khí'), 'Kỹ thuật Cơ điện tử',                     '7520114','110', NULL),
((SELECT id FROM department WHERE name='Khoa Cơ khí'), 'Kỹ thuật Dệt',                            '7520312','112', NULL),
((SELECT id FROM department WHERE name='Khoa Cơ khí'), 'Công nghệ Dệt May',                       '7540204','112', NULL),
((SELECT id FROM department WHERE name='Khoa Cơ khí'), 'Kỹ thuật Hệ thống Công nghiệp',           '7520118','128', NULL),
((SELECT id FROM department WHERE name='Khoa Cơ khí'), 'Logistics và Quản lý chuỗi cung ứng',     '7510605','128', NULL),
((SELECT id FROM department WHERE name='Khoa Cơ khí'), 'Kỹ thuật Nhiệt (Nhiệt lạnh)',             '7520115','140', NULL),
((SELECT id FROM department WHERE name='Khoa Cơ khí'), 'Bảo dưỡng Công nghiệp',                   '7510211','141', NULL),
-- CLC / chuyên sâu
((SELECT id FROM department WHERE name='Khoa Cơ khí'), 'Kỹ thuật Cơ khí (Chất lượng cao)',         '7520103','209','CLC'),
((SELECT id FROM department WHERE name='Khoa Cơ khí'), 'Kỹ thuật Cơ điện tử (Chất lượng cao)',     '7520114','210','CLC'),
((SELECT id FROM department WHERE name='Khoa Cơ khí'), 'Kỹ thuật Cơ điện tử - Chuyên ngành Kỹ thuật Robot (CLC)','7520114','211','CLC');

-- ------------------------------------------------------------------------------
-- KHOA KỸ THUẬT HÓA HỌC
-- ------------------------------------------------------------------------------
INSERT INTO major (department_id, name, major_code, program_code, note) VALUES
((SELECT id FROM department WHERE name='Khoa Kỹ thuật Hóa học'), 'Kỹ thuật Hóa học',            '7520301','112', NULL),
((SELECT id FROM department WHERE name='Khoa Kỹ thuật Hóa học'), 'Công nghệ Thực phẩm',        '7540101','114', NULL),
((SELECT id FROM department WHERE name='Khoa Kỹ thuật Hóa học'), 'Công nghệ Sinh học',         '7420101','114', NULL),
-- CLC
((SELECT id FROM department WHERE name='Khoa Kỹ thuật Hóa học'), 'Kỹ thuật Hóa học (Chất lượng cao)','7520301','213','CLC'),
((SELECT id FROM department WHERE name='Khoa Kỹ thuật Hóa học'), 'Công nghệ Thực phẩm (Chất lượng cao)','7540101','219','CLC');

-- ------------------------------------------------------------------------------
-- KHOA KỸ THUẬT XÂY DỰNG
-- ------------------------------------------------------------------------------
INSERT INTO major (department_id, name, major_code, program_code, note) VALUES
((SELECT id FROM department WHERE name='Khoa Kỹ thuật Xây dựng'), 'Kỹ thuật Xây dựng',                         '7580201','115', NULL),
((SELECT id FROM department WHERE name='Khoa Kỹ thuật Xây dựng'), 'Kỹ thuật Xây dựng Công trình Giao thông',   '7580205','115', NULL),
((SELECT id FROM department WHERE name='Khoa Kỹ thuật Xây dựng'), 'Kỹ thuật Xây dựng Công trình Thủy',         '7580202','115', NULL),
((SELECT id FROM department WHERE name='Khoa Kỹ thuật Xây dựng'), 'Kỹ thuật Xây dựng Công trình Biển',         '7580203','115', NULL),
((SELECT id FROM department WHERE name='Khoa Kỹ thuật Xây dựng'), 'Kỹ thuật Cơ sở Hạ tầng',                     '7580210','115', NULL),
((SELECT id FROM department WHERE name='Khoa Kỹ thuật Xây dựng'), 'Kỹ thuật Trắc địa - Bản đồ',                 '7520503','115', NULL),
((SELECT id FROM department WHERE name='Khoa Kỹ thuật Xây dựng'), 'Công nghệ Kỹ thuật Vật liệu Xây dựng',      '7510105','115', NULL),
((SELECT id FROM department WHERE name='Khoa Kỹ thuật Xây dựng'), 'Kiến trúc',                                  '7580101','117', NULL),
-- CLC
((SELECT id FROM department WHERE name='Khoa Kỹ thuật Xây dựng'), 'Kỹ thuật Xây dựng (Chất lượng cao)',         '7580201','215','CLC'),
((SELECT id FROM department WHERE name='Khoa Kỹ thuật Xây dựng'), 'Kiến trúc (Chuyên ngành Kiến trúc Cảnh quan) (CLC)','7580101','217','CLC');

-- ------------------------------------------------------------------------------
-- KHOA KỸ THUẬT ĐỊA CHẤT VÀ DẦU KHÍ
-- ------------------------------------------------------------------------------
INSERT INTO major (department_id, name, major_code, program_code, note) VALUES
((SELECT id FROM department WHERE name='Khoa Kỹ thuật Địa chất và Dầu khí'), 'Kỹ thuật Địa chất', '7520501','120', NULL),
((SELECT id FROM department WHERE name='Khoa Kỹ thuật Địa chất và Dầu khí'), 'Kỹ thuật Dầu khí',  '7520604','120', NULL),
-- CLC
((SELECT id FROM department WHERE name='Khoa Kỹ thuật Địa chất và Dầu khí'), 'Kỹ thuật Dầu khí (Chất lượng cao)','7520604','220','CLC');

-- ------------------------------------------------------------------------------
-- KHOA MÔI TRƯỜNG VÀ TÀI NGUYÊN
-- ------------------------------------------------------------------------------
INSERT INTO major (department_id, name, major_code, program_code, note) VALUES
((SELECT id FROM department WHERE name='Khoa Môi trường và Tài nguyên'), 'Kỹ thuật Môi trường',                 '7520320','125', NULL),
((SELECT id FROM department WHERE name='Khoa Môi trường và Tài nguyên'), 'Quản lý Tài nguyên và Môi trường',    '7850101','125', NULL),
-- CLC
((SELECT id FROM department WHERE name='Khoa Môi trường và Tài nguyên'), 'Quản lý và Công nghệ Môi trường (Chất lượng cao)','7850101','223','CLC');

-- ------------------------------------------------------------------------------
-- KHOA CÔNG NGHỆ VẬT LIỆU
-- ------------------------------------------------------------------------------
INSERT INTO major (department_id, name, major_code, program_code, note) VALUES
((SELECT id FROM department WHERE name='Khoa Công nghệ Vật liệu'), 'Kỹ thuật Vật liệu',          '7520309','129', NULL),
((SELECT id FROM department WHERE name='Khoa Công nghệ Vật liệu'), 'Kỹ thuật Vật liệu (Chất lượng cao CNC)','7520309','229','CLC');

-- ------------------------------------------------------------------------------
-- KHOA KHOA HỌC ỨNG DỤNG
-- ------------------------------------------------------------------------------
INSERT INTO major (department_id, name, major_code, program_code, note) VALUES
((SELECT id FROM department WHERE name='Khoa Khoa học Ứng dụng'), 'Vật lý Kỹ thuật',                     '7520401','137', NULL),
((SELECT id FROM department WHERE name='Khoa Khoa học Ứng dụng'), 'Cơ Kỹ thuật',                         '7520101','138', NULL),
-- CLC / tăng cường tiếng Nhật / chuyên ngành
((SELECT id FROM department WHERE name='Khoa Khoa học Ứng dụng'), 'Cơ Kỹ thuật (CLC tăng cường tiếng Nhật)','7520101','268','CLC JP'),
((SELECT id FROM department WHERE name='Khoa Khoa học Ứng dụng'), 'Vật lý Kỹ thuật (Chuyên ngành Kỹ thuật Y sinh - CLC)','7520401','237','CLC (BME)');

-- ------------------------------------------------------------------------------
-- KHOA KỸ THUẬT GIAO THÔNG
-- ------------------------------------------------------------------------------
INSERT INTO major (department_id, name, major_code, program_code, note) VALUES
((SELECT id FROM department WHERE name='Khoa Kỹ thuật Giao thông'), 'Kỹ thuật Ô tô',               '7520130','142', NULL),
((SELECT id FROM department WHERE name='Khoa Kỹ thuật Giao thông'), 'Kỹ thuật Tàu thủy',           '7520122','145', NULL),
((SELECT id FROM department WHERE name='Khoa Kỹ thuật Giao thông'), 'Kỹ thuật Hàng không',         '7520120','245','CLC'),
((SELECT id FROM department WHERE name='Khoa Kỹ thuật Giao thông'), 'Kỹ thuật Ô tô (Chất lượng cao)','7520130','242','CLC');

-- ------------------------------------------------------------------------------
-- KHOA QUẢN LÝ CÔNG NGHIỆP
-- ------------------------------------------------------------------------------
INSERT INTO major (department_id, name, major_code, program_code, note) VALUES
((SELECT id FROM department WHERE name='Khoa Quản lý Công nghiệp'), 'Quản lý Công nghiệp',          '7510601','123', NULL),
((SELECT id FROM department WHERE name='Khoa Quản lý Công nghiệp'), 'Quản lý Công nghiệp (Chất lượng cao)','7510601','223','CLC');


-- DATACORE: bỏ faculty, thêm major_id
ALTER TABLE `datacore`
  DROP COLUMN `faculty`,
  ADD COLUMN `major_id` BIGINT UNSIGNED NULL AFTER `email`,
  ADD KEY `idx_datacore_major` (`major_id`),
  ADD CONSTRAINT `fk_datacore_major`
    FOREIGN KEY (`major_id`) REFERENCES `major`(`id`)
    ON UPDATE CASCADE ON DELETE SET NULL;

-- USER: bỏ faculty, thêm major_id
ALTER TABLE `user`
  DROP COLUMN `faculty`,
  ADD COLUMN `major_id` BIGINT UNSIGNED NULL AFTER `last_name`,
  ADD KEY `idx_user_major` (`major_id`),
  ADD CONSTRAINT `fk_user_major`
    FOREIGN KEY (`major_id`) REFERENCES `major`(`id`)
    ON UPDATE CASCADE ON DELETE SET NULL;