package cn.tuyucheng.taketoday.dubbo.demo.consumer;

import cn.tuyucheng.taketoday.dubbo.demo.DemoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class Task implements CommandLineRunner {
   @DubboReference
   private DemoService demoService;

   @Override
   public void run(String... args) throws Exception {
      String result = demoService.sayHello("world");
      System.out.println("Receive result ======> " + result);

      new Thread(() -> {
         while (true) {
            try {
               Thread.sleep(1000);
               System.out.println(new Date() + " Receive result ======> " + demoService.sayHello("world"));
            } catch (InterruptedException e) {
               e.printStackTrace();
               Thread.currentThread().interrupt();
            }
         }
      }).start();
   }
}