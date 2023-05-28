package cn.tuyucheng.taketoday.blade.sample.interceptors;

import com.blade.mvc.RouteContext;
import com.blade.mvc.hook.WebHook;

public class TuyuchengMiddleware implements WebHook {

   private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TuyuchengMiddleware.class);

   @Override
   public boolean before(RouteContext context) {
      log.info("[TuyuchengMiddleware] called before Route method and other WebHooks");
      return true;
   }
}