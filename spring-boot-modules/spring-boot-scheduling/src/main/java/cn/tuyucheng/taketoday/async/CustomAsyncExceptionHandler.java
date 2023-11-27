package cn.tuyucheng.taketoday.async;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

   @Override
   public void handleUncaughtException(final Throwable throwable, final Method method, final Object... obj) {
      System.out.println(STR."Exception message - \{throwable.getMessage()}");
      System.out.println(STR."Method name - \{method.getName()}");
      for (final Object param : obj) {
         System.out.println(STR."Param - \{param}");
      }
   }
}