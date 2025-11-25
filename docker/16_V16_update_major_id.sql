UPDATE datacore
SET major_id = CASE hcmut_id
    WHEN 'NV001'    THEN 1
    WHEN 'GV001'    THEN 2
    WHEN '17123456' THEN 4
    WHEN '20123456' THEN 6
    WHEN '20125678' THEN 7
END
WHERE hcmut_id IN ('NV001','GV001','17123456','20123456','20125678');

UPDATE `user` u
JOIN datacore d ON d.hcmut_id = u.hcmut_id
SET u.major_id = d.major_id
WHERE u.major_id IS NULL;
