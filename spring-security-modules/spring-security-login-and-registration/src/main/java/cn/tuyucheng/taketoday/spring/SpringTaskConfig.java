package cn.tuyucheng.taketoday.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ComponentScan({"cn.tuyucheng.taketoday.task"})
public class SpringTaskConfig {

}
