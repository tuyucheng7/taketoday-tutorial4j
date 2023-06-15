package cn.tuyucheng.taketoday.servicemodule.external;

import cn.tuyucheng.taketoday.servicemodule.internal.LowercaseTextService;
import cn.tuyucheng.taketoday.servicemodule.internal.UppercaseTextService;

public class TextServiceFactory {

   private TextServiceFactory() {
   }

   public static TextService getTextService(String name) {
      return name.equalsIgnoreCase("lowercase") ? new LowercaseTextService() : new UppercaseTextService();
   }
}