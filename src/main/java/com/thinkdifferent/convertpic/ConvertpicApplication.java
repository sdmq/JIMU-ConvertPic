package com.thinkdifferent.convertpic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication
@EnableOpenApi
public class ConvertpicApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConvertpicApplication.class, args);
    }

}
