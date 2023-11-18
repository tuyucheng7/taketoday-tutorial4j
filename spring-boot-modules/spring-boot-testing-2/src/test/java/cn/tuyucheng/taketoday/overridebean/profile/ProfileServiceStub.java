package cn.tuyucheng.taketoday.overridebean.profile;

import cn.tuyucheng.taketoday.overridebean.Service;

public class ProfileServiceStub implements Service {

   public String helloWorld() {
      return "hello profile stub";
   }
}