package com.dc3;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableRabbit
@EnableTransactionManagement
@MapperScan(basePackages = "com.dc3.repository.mapper")
@SpringBootApplication(scanBasePackages = "com.dc3")
public class Dc3Application {

    public static void main(String[] args) {
        SpringApplication.run(Dc3Application.class, args);
    }
}
