package cn.tuyucheng.taketoday.mainmethod;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyApplication {

   public static void main(String[] args) {
      SpringApplication app = new SpringApplication(MyApplication.class);
      app.setBannerMode(Banner.Mode.OFF);
      app.setAdditionalProfiles("myprofile");
      app.run(args);
   }
}