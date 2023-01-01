package cn.tuyucheng.taketoday.micronaut.vs.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan("cn.tuyucheng.taketoday.micronaut.vs.springboot")
public class CompareApplication {
    public static void main(final String[] args) {
        SpringApplication.run(CompareApplication.class, args);
    }
}