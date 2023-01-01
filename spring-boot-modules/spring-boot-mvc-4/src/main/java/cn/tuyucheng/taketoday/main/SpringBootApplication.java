package cn.tuyucheng.taketoday.main;

import cn.tuyucheng.taketoday.boot.controller.servlet.HelloWorldServlet;
import cn.tuyucheng.taketoday.boot.controller.servlet.SpringHelloWorldServlet;
import cn.tuyucheng.taketoday.common.error.SpringHelloServletRegistrationBean;
import cn.tuyucheng.taketoday.common.resources.ExecutorServiceExitCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@EnableAutoConfiguration
@ComponentScan({
      "cn.tuyucheng.taketoday.common.error",
      "cn.tuyucheng.taketoday.common.error.controller",
      "cn.tuyucheng.taketoday.common.properties",
      "cn.tuyucheng.taketoday.common.resources",
      "cn.tuyucheng.taketoday.endpoints",
      "cn.tuyucheng.taketoday.service",
      "cn.tuyucheng.taketoday.monitor.jmx",
      "cn.tuyucheng.taketoday.boot.config"})
public class SpringBootApplication {

    private static ApplicationContext applicationContext;

    @GetMapping("/")
    String home() {
        return "TADA!!! You are in Spring Boot Actuator test application.";
    }

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(SpringBootApplication.class, args);
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(10);
    }

    @Bean
    public HelloWorldServlet helloWorldServlet() {
        return new HelloWorldServlet();
    }

    @Bean
    public SpringHelloServletRegistrationBean servletRegistrationBean() {
        SpringHelloServletRegistrationBean bean = new SpringHelloServletRegistrationBean(new SpringHelloWorldServlet(), "/springHelloWorld/*");
        bean.setLoadOnStartup(1);
        bean.addInitParameter("message", "SpringHelloWorldServlet special message");
        return bean;
    }

    @Bean
    @Autowired
    public ExecutorServiceExitCodeGenerator executorServiceExitCodeGenerator(ExecutorService executorService) {
        return new ExecutorServiceExitCodeGenerator(executorService);
    }

    public void shutDown(ExecutorServiceExitCodeGenerator executorServiceExitCodeGenerator) {
        SpringApplication.exit(applicationContext, executorServiceExitCodeGenerator);
    }
}