package cn.tuyucheng.taketoday.providermodule;

import cn.tuyucheng.taketoday.servicemodule.TextService;

public class LowercaseTextService implements TextService {
   @Override
   public String parseText(String text) {
      return text.toLowerCase();
   }
}