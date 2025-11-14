SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE subject;
CREATE TABLE `subject` (
  `id`   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255)    NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_subject_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


INSERT INTO subject (name) VALUES
-- Đại cương (11)
('Giải tích 1'),
('Vật lý 1'),
('Xác suất thống kê'),
('Đại số tuyến tính'),
('Hóa đại cương'),
('Giải tích 2'),
('Vật lý 2'),
('Phương pháp tính'),
('Thí nghiệm vật lý'),
('Vật lý bán dẫn'),
('Kỹ thuật lập trình'),

-- Chính trị – Xã hội (6)
('Pháp luật đại cương'),
('Triết học Mác-Lênin'),
('Kinh tế chính trị'),
('Chủ nghĩa xã hội khoa học'),
('Lịch sử Đảng'),
('Tư tưởng Hồ Chí Minh'),

-- Cơ sở ngành – Chuyên ngành (20, đã bỏ trùng “Kỹ thuật lập trình”)
('Chi tiết máy'),
('Phương pháp số'),
('Báo cáo thực tập trắc địa'),
('Đồ án nền móng'),
('Công nghệ phần mềm'),
('Điện tử công suất ứng dụng'),
('Đồ án công nghệ phần mềm'),
('Đo lường máy tính'),
('Hệ cơ sở dữ liệu'),
('Kỹ thuật Robot'),
('Lập trình nâng cao'),
('Lập trình web'),
('Thí nghiệm lập trình web'),
('Lý thuyết điều khiển nâng cao'),
('Mạng máy tính'),
('Scada'),
('Sức bền vật liệu'),
('Thủy khí'),
('Vật liệu xây dựng');

SET FOREIGN_KEY_CHECKS = 1;