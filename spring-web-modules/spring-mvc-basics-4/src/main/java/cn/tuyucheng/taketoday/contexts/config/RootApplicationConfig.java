package cn.tuyucheng.taketoday.contexts.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import cn.tuyucheng.taketoday.contexts.Greeting;

@Configuration
@ComponentScan(basePackages = { "cn.tuyucheng.taketoday.contexts.services" })
public class RootApplicationConfig {

    @Bean
    public Greeting greeting() {
        Greeting greeting = new Greeting();
        greeting.setMessage("Hello World !!");
        return greeting;
    }
}
