package com.example.demo.student;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.example.demo.student", "com.example.demo.common"})
@MapperScan("com.example.demo.student.mapper")
@EnableScheduling
public class StudentApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudentApplication.class, args);
        System.out.println("📋 学生服务启动成功！端口: 8082");
    }
}
