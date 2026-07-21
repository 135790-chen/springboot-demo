package com.example.demo.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Clazz;
import com.example.demo.entity.Student;
import com.example.demo.student.mapper.ClazzMapper;
import com.example.demo.student.mapper.StudentMapper;
import com.example.demo.common.exception.DuplicateStudentException;
import com.example.demo.student.service.StudentService;
import com.example.demo.vo.StudentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 学生业务逻辑层实现（ServiceImpl）
 *
 * 单表查询：MyBatis-Plus LambdaQueryWrapper
 * 多表查询：Mapper @Select LEFT JOIN → VO
 */
@Service
public class StudentServiceImpl implements StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private ClazzMapper clazzMapper;

    @Override
    @Transactional
    public Student addStudent(Student student) {
        if (student.getEmail() != null && !student.getEmail().isEmpty()) {
            LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Student::getEmail, student.getEmail());
            if (studentMapper.selectCount(wrapper) > 0) {
                log.warn("[Student] 重复提交被拦截: email={}", student.getEmail());
                throw new DuplicateStudentException("该邮箱已被注册: " + student.getEmail());
            }
        }
        studentMapper.insert(student);
        log.info("[Student] 新增学生成功: id={}, name={}, email={}", student.getId(), student.getStudentName(), student.getEmail());
        return student;
    }

    @Override
    @Transactional
    public boolean deleteStudent(Long id) {
        return studentMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean updateStudent(Student student) {
        LambdaUpdateWrapper<Student> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Student::getId, student.getId());
        if (student.getStudentName() != null) wrapper.set(Student::getStudentName, student.getStudentName());
        if (student.getEmail() != null) wrapper.set(Student::getEmail, student.getEmail());
        if (student.getGrade() != null) wrapper.set(Student::getGrade, student.getGrade());
        if (student.getPhone() != null) wrapper.set(Student::getPhone, student.getPhone());
        if (student.getBirthday() != null) wrapper.set(Student::getBirthday, student.getBirthday());
        if (student.getClassId() != null) wrapper.set(Student::getClassId, student.getClassId());
        return studentMapper.update(null, wrapper) > 0;
    }

    @Override
    public Student getStudentById(Long id) {
        return studentMapper.selectById(id);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentMapper.selectList(null);
    }

    @Override
    public List<Student> getStudentsByGrade(String grade) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getGrade, grade);
        return studentMapper.selectList(wrapper);
    }

    @Override
    public List<Student> searchStudentsByName(String keyword) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Student::getStudentName, keyword);
        return studentMapper.selectList(wrapper);
    }

    @Override
    public Page<Student> getStudentsByPage(int page, int size) {
        Page<Student> p = new Page<>(page, size);
        return studentMapper.selectPage(p, null);
    }

    @Override
    public Page<StudentVO> getStudentPage(int page, int size, String studentNo, String studentName,
                                           Long classId, Integer gender, Integer studentStatus) {
        // 直接调 Mapper LEFT JOIN 查询，一条 SQL 搞定
        return studentMapper.selectStudentVOPage(
                new Page<>(page, size), studentNo, studentName, classId, gender, studentStatus);
    }

    @Override
    public StudentVO getStudentVOById(Long id) {
        return studentMapper.selectStudentVOById(id);
    }
}
