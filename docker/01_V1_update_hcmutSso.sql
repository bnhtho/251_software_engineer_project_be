-- thêm 2 tài khoản SSO cho các ID đã có trong datacore
INSERT INTO `hcmut_sso` (email, password, hcmut_id) VALUES
('minhlv@hcmut.edu.vn',  '$2a$12$rYvpx3Yiqutf8pIW8AtqeO2jP4Hqtpfl8HnhaYDGNs.CIckpyWl8G', 'GV001'),
('20125678@hcmut.edu.vn','$2a$12$rYvpx3Yiqutf8pIW8AtqeO2jP4Hqtpfl8HnhaYDGNs.CIckpyWl8G', '20125678')
ON DUPLICATE KEY UPDATE email = VALUES(email);

ALTER TABLE `datacore`
  ADD CONSTRAINT `fk_datacore_hcmut_id`
  FOREIGN KEY (`hcmut_id`)
  REFERENCES `hcmut_sso`(`hcmut_id`)
  ON UPDATE CASCADE
  ON DELETE RESTRICT;