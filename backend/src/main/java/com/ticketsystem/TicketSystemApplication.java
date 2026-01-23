package com.ticketsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 工单系统主启动类
 */
@SpringBootApplication
@MapperScan("com.ticketsystem.mapper")
public class TicketSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketSystemApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("工单系统启动成功！");
        System.out.println("访问地址: http://localhost:8080/api");
        System.out.println("========================================\n");
    }
}
