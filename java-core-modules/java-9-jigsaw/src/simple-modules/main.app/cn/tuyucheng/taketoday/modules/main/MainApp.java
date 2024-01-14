package cn.tuyucheng.taketoday.modules.main;

import cn.tuyucheng.taketoday.modules.hello.HelloInterface;
import cn.tuyucheng.taketoday.modules.hello.HelloModules;

import java.util.ServiceLoader;

public class MainApp {

   public static void main(String[] args) {
      HelloModules.doSomething();

      Iterable<HelloInterface> services = ServiceLoader.load(HelloInterface.class);
      HelloInterface service = services.iterator().next();
      service.sayHello();
   }
}