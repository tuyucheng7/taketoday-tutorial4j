package cn.tuyucheng.taketoday.wiring.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"cn.tuyucheng.taketoday.dependency"})
public class ApplicationContextTestAutowiredName {
}
