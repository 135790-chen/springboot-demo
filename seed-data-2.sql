-- 补充未选课学生的选课记录
SET NAMES utf8mb4;

-- 2024级 软件1班 (4004陈静, 4005刘洋) — 大一已修课程
INSERT INTO edu_student_course (rel_id, student_id, course_id, score, rel_status, gmt_create, gmt_modified) VALUES
(5101, 4004, 3001, 88.0, 2, NOW(), NOW()),
(5102, 4004, 3002, 79.0, 2, NOW(), NOW()),
(5103, 4004, 3003, 91.0, 2, NOW(), NOW()),
(5104, 4004, 3004, NULL, 1, NOW(), NOW()),
(5105, 4005, 3001, 62.0, 2, NOW(), NOW()),
(5106, 4005, 3002, 58.0, 2, NOW(), NOW()),
(5107, 4005, 3003, 71.0, 2, NOW(), NOW()),
(5108, 4005, 3006, NULL, 1, NOW(), NOW());

-- 2024级 软件2班 (4008周洁~4010郑爽)
INSERT INTO edu_student_course (rel_id, student_id, course_id, score, rel_status, gmt_create, gmt_modified) VALUES
(5109, 4008, 3001, 85.0, 2, NOW(), NOW()),
(5110, 4008, 3002, 90.0, 2, NOW(), NOW()),
(5111, 4008, 3003, 87.0, 2, NOW(), NOW()),
(5112, 4008, 3005, NULL, 1, NOW(), NOW()),
(5113, 4009, 3001, 74.0, 2, NOW(), NOW()),
(5114, 4009, 3002, 68.0, 2, NOW(), NOW()),
(5115, 4009, 3004, NULL, 1, NOW(), NOW()),
(5116, 4010, 3001, 93.0, 2, NOW(), NOW()),
(5117, 4010, 3002, 86.0, 2, NOW(), NOW()),
(5118, 4010, 3003, 95.0, 2, NOW(), NOW()),
(5119, 4010, 3005, NULL, 1, NOW(), NOW()),
(5120, 4010, 3006, NULL, 1, NOW(), NOW());

-- 2024级 计科1班 (4012褚芳~4015韩冰)
INSERT INTO edu_student_course (rel_id, student_id, course_id, score, rel_status, gmt_create, gmt_modified) VALUES
(5121, 4012, 3001, 82.0, 2, NOW(), NOW()),
(5122, 4012, 3002, 76.0, 2, NOW(), NOW()),
(5123, 4012, 3003, 89.0, 2, NOW(), NOW()),
(5124, 4012, 3004, NULL, 1, NOW(), NOW()),
(5125, 4013, 3001, 70.0, 2, NOW(), NOW()),
(5126, 4013, 3002, 64.0, 2, NOW(), NOW()),
(5127, 4013, 3003, 55.0, 2, NOW(), NOW()),
(5128, 4013, 3006, NULL, 1, NOW(), NOW()),
(5129, 4014, 3001, 96.0, 2, NOW(), NOW()),
(5130, 4014, 3002, 92.0, 2, NOW(), NOW()),
(5131, 4014, 3003, 98.0, 2, NOW(), NOW()),
(5132, 4014, 3013, NULL, 1, NOW(), NOW()),
(5133, 4015, 3001, 78.0, 2, NOW(), NOW()),
(5134, 4015, 3003, 83.0, 2, NOW(), NOW()),
(5135, 4015, 3005, NULL, 1, NOW(), NOW());

-- 2024级 人工智能1班 (4017朱婷~4020许鹏)
INSERT INTO edu_student_course (rel_id, student_id, course_id, score, rel_status, gmt_create, gmt_modified) VALUES
(5136, 4017, 3001, 84.0, 2, NOW(), NOW()),
(5137, 4017, 3002, 88.0, 2, NOW(), NOW()),
(5138, 4017, 3003, 79.0, 2, NOW(), NOW()),
(5139, 4017, 3004, NULL, 1, NOW(), NOW()),
(5140, 4018, 3001, 61.0, 2, NOW(), NOW()),
(5141, 4018, 3002, 57.0, 2, NOW(), NOW()),
(5142, 4018, 3003, 69.0, 2, NOW(), NOW()),
(5143, 4019, 3001, 90.0, 2, NOW(), NOW()),
(5144, 4019, 3002, 87.0, 2, NOW(), NOW()),
(5145, 4019, 3003, 93.0, 2, NOW(), NOW()),
(5146, 4019, 3006, NULL, 1, NOW(), NOW()),
(5147, 4019, 3010, NULL, 1, NOW(), NOW()),
(5148, 4020, 3001, 73.0, 2, NOW(), NOW()),
(5149, 4020, 3003, 77.0, 2, NOW(), NOW()),
(5150, 4020, 3005, NULL, 1, NOW(), NOW());

-- 2023级 软件1班 (4024张晨已毕业, 4025孔琳)
INSERT INTO edu_student_course (rel_id, student_id, course_id, score, rel_status, gmt_create, gmt_modified) VALUES
(5151, 4024, 3004, 80.0, 2, NOW(), NOW()),
(5152, 4024, 3005, 76.0, 2, NOW(), NOW()),
(5153, 4024, 3006, 82.0, 2, NOW(), NOW()),
(5154, 4025, 3004, 91.0, 2, NOW(), NOW()),
(5155, 4025, 3005, 85.0, 2, NOW(), NOW()),
(5156, 4025, 3006, 88.0, 2, NOW(), NOW()),
(5157, 4025, 3013, 79.0, 2, NOW(), NOW()),
(5158, 4025, 3007, NULL, 1, NOW(), NOW()),
(5159, 4025, 3008, NULL, 1, NOW(), NOW());

-- 2023级 软件2班 (4028华磊~4030魏凯)
INSERT INTO edu_student_course (rel_id, student_id, course_id, score, rel_status, gmt_create, gmt_modified) VALUES
(5160, 4028, 3004, 72.0, 2, NOW(), NOW()),
(5161, 4028, 3005, 68.0, 2, NOW(), NOW()),
(5162, 4028, 3006, 75.0, 2, NOW(), NOW()),
(5163, 4028, 3007, NULL, 1, NOW(), NOW()),
(5164, 4029, 3004, 94.0, 2, NOW(), NOW()),
(5165, 4029, 3005, 90.0, 2, NOW(), NOW()),
(5166, 4029, 3006, 92.0, 2, NOW(), NOW()),
(5167, 4029, 3013, 87.0, 2, NOW(), NOW()),
(5168, 4029, 3008, NULL, 1, NOW(), NOW()),
(5169, 4029, 3009, NULL, 1, NOW(), NOW()),
(5170, 4030, 3004, 66.0, 2, NOW(), NOW()),
(5171, 4030, 3005, 59.0, 2, NOW(), NOW()),
(5172, 4030, 3013, 71.0, 2, NOW(), NOW()),
(5173, 4030, 3011, NULL, 1, NOW(), NOW());

-- 2023级 计科1班 (4032姜珊~4035邹阳, 4034谢婉休学)
INSERT INTO edu_student_course (rel_id, student_id, course_id, score, rel_status, gmt_create, gmt_modified) VALUES
(5174, 4032, 3004, 85.0, 2, NOW(), NOW()),
(5175, 4032, 3005, 81.0, 2, NOW(), NOW()),
(5176, 4032, 3006, 88.0, 2, NOW(), NOW()),
(5177, 4032, 3013, 83.0, 2, NOW(), NOW()),
(5178, 4032, 3007, NULL, 1, NOW(), NOW()),
(5179, 4033, 3004, 58.0, 2, NOW(), NOW()),
(5180, 4033, 3005, 52.0, 2, NOW(), NOW()),
(5181, 4033, 3006, 63.0, 2, NOW(), NOW()),
(5182, 4033, 3010, NULL, 1, NOW(), NOW()),
(5183, 4034, 3004, 77.0, 2, NOW(), NOW()),
(5184, 4034, 3005, 73.0, 2, NOW(), NOW()),
(5185, 4035, 3004, 89.0, 2, NOW(), NOW()),
(5186, 4035, 3005, 92.0, 2, NOW(), NOW()),
(5187, 4035, 3006, 86.0, 2, NOW(), NOW()),
(5188, 4035, 3013, 91.0, 2, NOW(), NOW()),
(5189, 4035, 3007, NULL, 1, NOW(), NOW()),
(5190, 4035, 3009, NULL, 1, NOW(), NOW()),
(5191, 4035, 3011, NULL, 1, NOW(), NOW());

-- 2023级 数据科学1班 (4037潘悦~4040彭冲)
INSERT INTO edu_student_course (rel_id, student_id, course_id, score, rel_status, gmt_create, gmt_modified) VALUES
(5192, 4037, 3004, 82.0, 2, NOW(), NOW()),
(5193, 4037, 3005, 78.0, 2, NOW(), NOW()),
(5194, 4037, 3006, 84.0, 2, NOW(), NOW()),
(5195, 4037, 3007, NULL, 1, NOW(), NOW()),
(5196, 4038, 3004, 71.0, 2, NOW(), NOW()),
(5197, 4038, 3005, 67.0, 2, NOW(), NOW()),
(5198, 4038, 3006, 74.0, 2, NOW(), NOW()),
(5199, 4038, 3013, 69.0, 2, NOW(), NOW()),
(5200, 4038, 3008, NULL, 1, NOW(), NOW()),
(5201, 4039, 3004, 95.0, 2, NOW(), NOW()),
(5202, 4039, 3005, 93.0, 2, NOW(), NOW()),
(5203, 4039, 3006, 97.0, 2, NOW(), NOW()),
(5204, 4039, 3013, 90.0, 2, NOW(), NOW()),
(5205, 4039, 3007, NULL, 1, NOW(), NOW()),
(5206, 4039, 3010, NULL, 1, NOW(), NOW()),
(5207, 4040, 3004, 68.0, 2, NOW(), NOW()),
(5208, 4040, 3005, 72.0, 2, NOW(), NOW()),
(5209, 4040, 3006, 65.0, 2, NOW(), NOW()),
(5210, 4040, 3011, NULL, 1, NOW(), NOW()),
(5211, 4040, 3014, NULL, 1, NOW(), NOW());

-- 额外跨年级选修（大三课开放给优秀大二生）
INSERT INTO edu_student_course (rel_id, student_id, course_id, score, rel_status, gmt_create, gmt_modified) VALUES
(5212, 4014, 3010, NULL, 1, NOW(), NOW()),
(5213, 4039, 3012, NULL, 1, NOW(), NOW()),
(5214, 4029, 3012, NULL, 1, NOW(), NOW()),
(5215, 4035, 3012, NULL, 1, NOW(), NOW()),
(5216, 4022, 3012, NULL, 1, NOW(), NOW()),
(5217, 4031, 3014, NULL, 1, NOW(), NOW()),
(5218, 4021, 3014, NULL, 1, NOW(), NOW());
