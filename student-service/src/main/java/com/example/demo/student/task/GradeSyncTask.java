package com.example.demo.student.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.demo.entity.Clazz;
import com.example.demo.entity.Student;
import com.example.demo.student.mapper.ClazzMapper;
import com.example.demo.student.mapper.StudentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 年级自动同步 — 当班级表 grade 变更后，同步更新学生表冗余的 grade 字段
 */
@Component
public class GradeSyncTask {

    private static final Logger log = LoggerFactory.getLogger(GradeSyncTask.class);

    @Autowired private StudentMapper studentMapper;
    @Autowired private ClazzMapper clazzMapper;

    @Scheduled(cron = "0 0 3 * * ?")
    public void syncStudentGrade() {
        log.info("[GradeSync] 开始同步学生年级...");
        int updated = 0;
        var classes = clazzMapper.selectList(
                new LambdaQueryWrapper<Clazz>().eq(Clazz::getClassDeleted, 0));
        for (Clazz c : classes) {
            if (c.getId() == null || c.getGrade() == null) continue;
            var wrapper = new LambdaUpdateWrapper<Student>()
                    .eq(Student::getClassId, c.getId())
                    .ne(Student::getGrade, c.getGrade())
                    .set(Student::getGrade, c.getGrade());
            updated += studentMapper.update(null, wrapper);
        }
        log.info("[GradeSync] 同步完成，更新 {} 名学生年级", updated);
    }
}
