package cn.tuyucheng.taketoday.cachedrequest;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * To initialize the WebApplication, Please see
 * {@link cn.tuyucheng.taketoday.spring.config.MainWebAppInitializer}
 */

@EnableWebMvc
@Configuration
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.cachedrequest")
public class HttpRequestDemoConfig implements WebMvcConfigurer {

}