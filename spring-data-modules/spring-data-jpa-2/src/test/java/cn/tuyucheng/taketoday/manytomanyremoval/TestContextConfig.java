package cn.tuyucheng.taketoday.manytomanyremoval;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:/manytomanyremoval/test.properties")
@ComponentScan("cn.tuyucheng.taketoday.manytomanyremoval")
public class TestContextConfig {
}