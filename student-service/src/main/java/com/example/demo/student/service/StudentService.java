package com.example.demo.student.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Student;
import com.example.demo.vo.StudentCourseVO;
import com.example.demo.vo.StudentVO;

import java.util.List;

/**
 * 学生业务逻辑接口
 * 定义业务方法的契约，具体实现在 impl 包中。
 * Controller 层只依赖此接口，不关心具体实现。
 */
public interface StudentService {

    /**
     * 添加学生
     */
    Student addStudent(Student student);

    /**
     * 根据 ID 删除学生
     */
    boolean deleteStudent(Long id);

    /**
     * 更新学生信息
     */
    boolean updateStudent(Student student);
    /**
     * 根据 ID 查询学生
     */
    Student getStudentById(Long id);

    /**
     * 查询所有学生
     */
    List<Student> getAllStudents();

    /**
     * 根据年级查询学生
     */
    List<Student> getStudentsByGrade(String grade);

    /**
     * 根据姓名模糊查询
     */
    List<Student> searchStudentsByName(String keyword);

    /**
     * 分页查询所有学生
     */
    Page<Student> getStudentsByPage(int page, int size);

    /**
     * 分页查询学生（关联班级，返回 VO）
     */
    Page<StudentVO> getStudentPage(int page, int size, String studentNo, String studentName,
                                   Long classId, Integer gender, Integer studentStatus);

    /**
     * 查询学生详情（关联班级，返回 VO）
     */
    StudentVO getStudentVOById(Long id);
}
