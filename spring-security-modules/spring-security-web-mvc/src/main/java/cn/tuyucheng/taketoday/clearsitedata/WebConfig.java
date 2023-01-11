package cn.tuyucheng.taketoday.clearsitedata;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
@ComponentScan(basePackages = {"cn.tuyucheng.taketoday.clearsitedata"})
public class WebConfig implements WebMvcConfigurer {
}