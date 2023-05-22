package cn.tuyucheng.taketoday.dozer;

import com.github.dozermapper.spring.DozerBeanMapperFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import java.io.IOException;

@SpringBootApplication
public class DozerApp {

   public static void main(String[] args) {
      SpringApplication.run(DozerApp.class, args);
   }

   @Bean
   public DozerBeanMapperFactoryBean dozerMapper(@Value("classpath:dozer/*.xml") Resource[] resources) throws IOException {
      DozerBeanMapperFactoryBean dozerBeanMapperFactoryBean = new DozerBeanMapperFactoryBean();
      dozerBeanMapperFactoryBean.setMappingFiles(resources);
      return dozerBeanMapperFactoryBean;
   }
}