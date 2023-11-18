package cn.tuyucheng.taketoday.overridebean.overridebeandefinition;

import cn.tuyucheng.taketoday.overridebean.Service;

public class OverrideBeanDefinitionServiceStub implements Service {

   public String helloWorld() {
      return "hello no profile stub";
   }
}