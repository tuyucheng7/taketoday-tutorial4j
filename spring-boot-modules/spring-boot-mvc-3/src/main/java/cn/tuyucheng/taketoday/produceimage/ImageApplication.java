package cn.tuyucheng.taketoday.produceimage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan("cn.tuyucheng.taketoday.produceimage")
public class ImageApplication {
    public static void main(final String[] args) {
        SpringApplication.run(ImageApplication.class, args);
    }
}