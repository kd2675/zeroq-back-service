package com.zeroq.back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ZeroqBackServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZeroqBackServiceApplication.class, args);
    }

}
