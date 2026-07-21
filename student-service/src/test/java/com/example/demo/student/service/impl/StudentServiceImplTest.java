package com.example.demo.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.demo.common.exception.DuplicateStudentException;
import com.example.demo.entity.Student;
import com.example.demo.student.mapper.StudentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * StudentServiceImpl 单元测试
 * <p>
 * 测试核心增删改查逻辑，mock StudentMapper。
 */
class StudentServiceImplTest {

    private StudentServiceImpl service;
    private StudentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = mock(StudentMapper.class);
        service = new StudentServiceImpl();
        try {
            var field = StudentServiceImpl.class.getDeclaredField("studentMapper");
            field.setAccessible(true);
            field.set(service, mapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ── 新增 ──

    @Test
    void addStudent_normal_success() {
        Student input = new Student();
        input.setStudentName("张三");
        input.setEmail("zhangsan@qq.com");
        input.setGrade("大一");

        // insert 后自动回填 id
        when(mapper.insert(any(Student.class))).thenAnswer(inv -> {
            Student s = inv.getArgument(0);
            s.setId(1L);
            return 1;
        });
        when(mapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        Student result = service.addStudent(input);

        assertNotNull(result.getId(), "新增后 id 应自动回填");
        assertEquals("张三", result.getStudentName());
        verify(mapper).insert(any(Student.class));
    }

    @Test
    void addStudent_duplicateEmail_throwsException() {
        // 第一次查重就命中
        when(mapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        Student input = new Student();
        input.setEmail("dup@qq.com");
        input.setStudentName("重复");
        input.setGrade("大二");

        assertThrows(DuplicateStudentException.class,
                () -> service.addStudent(input),
                "重复邮箱应抛出 DuplicateStudentException");
        verify(mapper, never()).insert(any());
    }

    // ── 更新（只设 id，不触发 LambdaUpdateWrapper.set()，该方法需 Spring 初始化 lambda 缓存）──

    @Test
    void updateStudent_success_returnsTrue() {
        Student input = new Student();
        input.setId(1L);
        // 其他字段为 null → updateStudent() 只拼接 eq(id) 条件，不执行 set()

        when(mapper.update(isNull(), any(LambdaUpdateWrapper.class))).thenReturn(1);

        assertTrue(service.updateStudent(input));
        verify(mapper).update(isNull(), any(LambdaUpdateWrapper.class));
    }

    @Test
    void updateStudent_notExists_returnsFalse() {
        Student input = new Student();
        input.setId(999L);

        when(mapper.update(isNull(), any(LambdaUpdateWrapper.class))).thenReturn(0);

        assertFalse(service.updateStudent(input));
    }

    // ── 查询 ──

    @Test
    void getStudentsByGrade_hasData_returnsList() {
        Student s1 = buildStudent(1L, "张三", "大一");
        Student s2 = buildStudent(2L, "李四", "大一");

        when(mapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(s1, s2));

        List<Student> result = service.getStudentsByGrade("大一");

        assertEquals(2, result.size());
        assertEquals("张三", result.get(0).getStudentName());
        assertEquals("李四", result.get(1).getStudentName());
    }

    @Test
    void searchStudentsByName_keywordMatch_returnsMatching() {
        Student s = buildStudent(1L, "张三丰", "大三");
        when(mapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(s));

        List<Student> result = service.searchStudentsByName("张");

        assertEquals(1, result.size());
        assertEquals("张三丰", result.get(0).getStudentName());
    }

    // ── 删除 ──

    @Test
    void deleteStudent_exists_returnsTrue() {
        when(mapper.deleteById(1L)).thenReturn(1);

        assertTrue(service.deleteStudent(1L));
        verify(mapper).deleteById(1L);
    }

    @Test
    void deleteStudent_notExists_returnsFalse() {
        when(mapper.deleteById(999L)).thenReturn(0);

        assertFalse(service.deleteStudent(999L));
    }

    // ── 工具方法 ──

    private Student buildStudent(Long id, String name, String grade) {
        Student s = new Student();
        s.setId(id);
        s.setStudentName(name);
        s.setEmail(name + "@test.com");
        s.setGrade(grade);
        return s;
    }
}
