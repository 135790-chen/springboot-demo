package com.example.demo.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(
        scanBasePackages = {"com.example.demo.gateway", "com.example.demo.common"},
        exclude = {DataSourceAutoConfiguration.class}
)
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║  🚪 API 网关启动成功！端口: 8080                     ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.println("║  📋 前端页面 : http://localhost:8080                 ║");
        System.out.println("║  📖 API 文档 : http://localhost:8080/doc.html        ║");
        System.out.println("║  ⚙️  Nacos   : http://localhost:8848/nacos           ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }
}
