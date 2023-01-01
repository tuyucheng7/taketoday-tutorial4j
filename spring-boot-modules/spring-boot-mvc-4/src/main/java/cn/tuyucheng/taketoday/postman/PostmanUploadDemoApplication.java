package cn.tuyucheng.taketoday.postman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class PostmanUploadDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostmanUploadDemoApplication.class, args);
    }
}