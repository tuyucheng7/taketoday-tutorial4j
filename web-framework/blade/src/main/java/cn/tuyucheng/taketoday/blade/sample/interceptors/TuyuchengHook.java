package cn.tuyucheng.taketoday.blade.sample.interceptors;

import com.blade.ioc.annotation.Bean;
import com.blade.mvc.RouteContext;
import com.blade.mvc.hook.WebHook;

@Bean
public class TuyuchengHook implements WebHook {

   private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TuyuchengHook.class);

   @Override
   public boolean before(RouteContext ctx) {
      log.info("[TuyuchengHook] called before Route method");
      return true;
   }
}