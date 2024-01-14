package cn.tuyucheng.taketoday.consumermodule;

import cn.tuyucheng.taketoday.servicemodule.external.TextService;
import cn.tuyucheng.taketoday.servicemodule.external.TextServiceFactory;

public class Application {

   public static void main(String args[]) {
      TextService textService = TextServiceFactory.getTextService("lowercase");
      System.out.println(textService.processText("Hello from Tuyucheng!"));
   }

}
