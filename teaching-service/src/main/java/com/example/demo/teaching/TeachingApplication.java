package com.example.demo.teaching;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.example.demo.teaching", "com.example.demo.common"})
@MapperScan("com.example.demo.teaching.mapper")
@EnableScheduling
public class TeachingApplication {
    public static void main(String[] args) {
        SpringApplication.run(TeachingApplication.class, args);
    }
}
