package cn.tuyucheng.taketoday.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.tuyucheng.taketoday.web.controller.handlermapping.BeanNameHandlerMappingController;
import cn.tuyucheng.taketoday.web.controller.handlermapping.WelcomeController;


@Configuration
public class HandlerMappingDefaultConfig {

    @Bean("/welcome")
    public BeanNameHandlerMappingController beanNameHandlerMapping() {
        return new BeanNameHandlerMappingController();
    }

    @Bean
    public WelcomeController welcomeDefaultMappingConfig() {
        return new WelcomeController();
    }

}