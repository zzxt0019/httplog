package com.github.zzxt0019.httplog.example.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@MapperScan("com.github.zzxt0019.httplog.example.common.mapper")
@SpringBootApplication(scanBasePackages = "com.github.zzxt0019")
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

}
