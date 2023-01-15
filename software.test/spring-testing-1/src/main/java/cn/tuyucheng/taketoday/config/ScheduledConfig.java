package cn.tuyucheng.taketoday.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ComponentScan("cn.tuyucheng.taketoday.scheduled")
public class ScheduledConfig {

}