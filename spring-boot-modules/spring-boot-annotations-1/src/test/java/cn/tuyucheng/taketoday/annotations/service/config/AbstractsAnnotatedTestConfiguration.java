package cn.tuyucheng.taketoday.annotations.service.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;

@TestConfiguration
@ComponentScan("cn.tuyucheng.taketoday.annotations.service.abstracts")
public class AbstractsAnnotatedTestConfiguration {

}