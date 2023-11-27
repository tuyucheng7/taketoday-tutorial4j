package cn.tuyucheng.taketoday.yaml;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyApplication implements CommandLineRunner {

   @Autowired
   private YAMLConfig myConfig;

   public static void main(String[] args) {
      SpringApplication app = new SpringApplication(MyApplication.class);
      app.run();
   }

   public void run(String... args) {
      System.out.println(STR."using environment:\{myConfig.getEnvironment()}");
      System.out.println(STR."name:\{myConfig.getName()}");
      System.out.println(STR."enabled:\{myConfig.isEnabled()}");
      System.out.println(STR."servers:\{myConfig.getServers()}");

      if ("testing".equalsIgnoreCase(myConfig.getEnvironment())) {
         System.out.println(STR."external:\{myConfig.getExternal()}");
         System.out.println(STR."map:\{myConfig.getMap()}");

         YAMLConfig.Idm idm = myConfig.getComponent().getIdm();
         YAMLConfig.Service service = myConfig.getComponent().getService();
         System.out.println("Idm:");
         System.out.println(STR."   Url: \{idm.getUrl()}");
         System.out.println(STR."   User: \{idm.getUser()}");
         System.out.println(STR."   Password: \{idm.getPassword()}");
         System.out.println(STR."   Description: \{idm.getDescription()}");
         System.out.println("Service:");
         System.out.println(STR."   Url: \{service.getUrl()}");
         System.out.println(STR."   Token: \{service.getToken()}");
         System.out.println(STR."   Description: \{service.getDescription()}");
      }
   }
}