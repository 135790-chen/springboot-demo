package com.example.demo.student.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.demo.entity.Student;
import com.example.demo.student.mapper.StudentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 学生状态自动更新 — 毕业年份已到的学生从「在读」→「毕业」
 * 规则：enrollment_year + 4 <= 当前年份 且 状态=在读
 */
@Component
public class StudentStatusTask {

    private static final Logger log = LoggerFactory.getLogger(StudentStatusTask.class);

    @Autowired private StudentMapper studentMapper;

    @Scheduled(cron = "0 0 2 * * ?")
    public void autoGraduate() {
        int currentYear = LocalDate.now().getYear();
        log.info("[StudentStatus] 开始检查学生毕业状态，当前年份: {}", currentYear);

        // 设学制为 4 年（专科 3 年可调整）
        int yearsToGraduate = 4;
        int updated = 0;

        for (int year = currentYear - 10; year <= currentYear; year++) {
            if (year + yearsToGraduate <= currentYear) {
                var wrapper = new LambdaUpdateWrapper<Student>()
                        .eq(Student::getEnrollmentYear, String.valueOf(year))
                        .eq(Student::getStudentStatus, 1)    // 在读
                        .eq(Student::getStudentDeleted, 0)
                        .set(Student::getStudentStatus, 3);   // 毕业
                updated += studentMapper.update(null, wrapper);
            }
        }

        if (updated > 0) {
            log.info("[StudentStatus] 自动毕业 {} 名学生", updated);
        }
    }
}
