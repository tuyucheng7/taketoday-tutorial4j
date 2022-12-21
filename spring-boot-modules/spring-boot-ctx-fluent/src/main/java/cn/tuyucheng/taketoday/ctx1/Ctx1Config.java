package cn.tuyucheng.taketoday.ctx1;

import cn.tuyucheng.taketoday.parent.HomeService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("cn.tuyucheng.taketoday.ctx1")
@PropertySource("classpath:ctx1.properties")
@EnableAutoConfiguration
public class Ctx1Config {

    @Bean
    public HomeService homeService() {
        return new GreetingService();
    }
}