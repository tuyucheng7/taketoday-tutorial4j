package cn.tuyucheng.taketoday.utils;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.security.RolesAllowed;

@SpringBootApplication
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.utils")
public class UtilsApplication {

    @RolesAllowed("*")
    public static void main(String[] args) {
        SpringApplication.run(UtilsApplication.class, args);
    }
}