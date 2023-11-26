package cn.tuyucheng.taketoday.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

   @Bean
   public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
      return new PropertySourcesPlaceholderConfigurer();
   }

   @Override
   public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {
      configurer.enable();
   }

   @Override
   public void addViewControllers(final ViewControllerRegistry registry) {
      super.addViewControllers(registry);
      registry.addViewController("/").setViewName("forward:/index");
      registry.addViewController("/index");
      registry.addViewController("/login");
   }

   @Override
   public void addResourceHandlers(final ResourceHandlerRegistry registry) {
      registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
   }

}