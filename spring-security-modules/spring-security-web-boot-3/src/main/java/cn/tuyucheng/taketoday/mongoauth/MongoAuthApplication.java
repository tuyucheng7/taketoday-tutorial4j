package cn.tuyucheng.taketoday.mongoauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import cn.tuyucheng.taketoday.mongoauth.config.MongoConfig;
import cn.tuyucheng.taketoday.mongoauth.config.SecurityConfig;

@SpringBootApplication
@Import({ SecurityConfig.class, MongoConfig.class })
public class MongoAuthApplication {

    public static void main(String... args) {
        SpringApplication.run(MongoAuthApplication.class, args);
    }

}
