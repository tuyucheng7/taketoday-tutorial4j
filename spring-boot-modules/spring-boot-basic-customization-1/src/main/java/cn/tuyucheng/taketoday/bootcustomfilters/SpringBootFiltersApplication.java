package cn.tuyucheng.taketoday.bootcustomfilters;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

/**
 * Boot application
 *
 * @author tuyucheng
 */
@Profile("filter")
@SpringBootApplication(scanBasePackages = "cn.tuyucheng.taketoday.bootcustomfilters.*")
public class SpringBootFiltersApplication {

   public static void main(String[] args) {
      SpringApplication.run(SpringBootFiltersApplication.class, args);
   }
}