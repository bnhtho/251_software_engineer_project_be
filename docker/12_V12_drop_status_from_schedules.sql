-- V12: Drop status column from tutor_schedule and student_schedule tables
-- Lý do: Status column không cần thiết trong schedule tables
-- Drop status column from tutor_schedule
ALTER TABLE tutor_schedule
DROP COLUMN `status`;

-- Drop status column from student_schedule
ALTER TABLE student_schedule
DROP COLUMN `status`;
