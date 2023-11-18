package cn.tuyucheng.taketoday.overridebean.primary;

import cn.tuyucheng.taketoday.overridebean.Service;

public class PrimaryServiceStub implements Service {

   public String helloWorld() {
      return "hello primary stub";
   }
}