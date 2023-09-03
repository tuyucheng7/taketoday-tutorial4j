package cn.tuyucheng.taketoday.selfinvocation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@EnableCaching(mode = AdviceMode.ASPECTJ)
@ComponentScan(basePackages = {"cn.tuyucheng.taketoday"}, excludeFilters = {
      @ComponentScan.Filter(type = FilterType.ANNOTATION,
            value = {SpringBootApplication.class})
})
public class CompileTimeWeavingApplication {
   public static void main(String[] args) {
      SpringApplication.run(CompileTimeWeavingApplication.class, args);
   }
}