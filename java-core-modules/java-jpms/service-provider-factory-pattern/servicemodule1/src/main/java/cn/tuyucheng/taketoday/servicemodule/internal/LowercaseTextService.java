package cn.tuyucheng.taketoday.servicemodule.internal;

import cn.tuyucheng.taketoday.servicemodule.external.TextService;

public class LowercaseTextService implements TextService {

   @Override
   public String processText(String text) {
      return text.toLowerCase();
   }
}