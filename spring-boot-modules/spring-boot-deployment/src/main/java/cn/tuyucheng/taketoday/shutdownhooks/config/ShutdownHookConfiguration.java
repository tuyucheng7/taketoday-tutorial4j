package cn.tuyucheng.taketoday.shutdownhooks.config;

import cn.tuyucheng.taketoday.shutdownhooks.beans.Bean3;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContextListener;

@Configuration
public class ShutdownHookConfiguration {

    @Bean(destroyMethod = "destroy")
    public Bean3 initializeBean3() {
        return new Bean3();
    }

    @Bean
    ServletListenerRegistrationBean<ServletContextListener> servletListener() {
        ServletListenerRegistrationBean<ServletContextListener> srb = new ServletListenerRegistrationBean<>();
        srb.setListener(new ExampleServletContextListener());
        return srb;
    }
}