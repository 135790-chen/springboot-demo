package com.example.demo.student.controller;

import com.example.demo.common.GlobalExceptionHandler;
import com.example.demo.entity.Student;
import com.example.demo.student.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StudentControllerTest {

    private MockMvc mockMvc;
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        studentService = mock(StudentService.class);
        StudentController controller = new StudentController();
        try {
            var field = StudentController.class.getDeclaredField("studentService");
            field.setAccessible(true);
            field.set(controller, studentService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAll_success() throws Exception {
        Student s = new Student();
        s.setId(1L);
        s.setStudentName("张三");
        when(studentService.getAllStudents()).thenReturn(List.of(s));

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].studentName").value("张三"));
    }

    @Test
    void getById_success() throws Exception {
        Student s = new Student();
        s.setId(1L);
        s.setStudentName("张三");
        when(studentService.getStudentById(1L)).thenReturn(s);

        mockMvc.perform(get("/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.studentName").value("张三"));
    }

    @Test
    void getById_notFound() throws Exception {
        when(studentService.getStudentById(999L)).thenReturn(null);

        mockMvc.perform(get("/students/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void add_success() throws Exception {
        Student s = new Student();
        s.setId(1L);
        s.setStudentName("新同学");
        when(studentService.addStudent(any())).thenReturn(s);

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"studentNo\":\"STU20250001\",\"studentName\":\"新同学\",\"grade\":\"大二\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void update_noId() throws Exception {
        mockMvc.perform(put("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"studentName\":\"无ID\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void delete_success() throws Exception {
        when(studentService.deleteStudent(1L)).thenReturn(true);

        mockMvc.perform(delete("/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void delete_notFound() throws Exception {
        when(studentService.deleteStudent(999L)).thenReturn(false);

        mockMvc.perform(delete("/students/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }
}
