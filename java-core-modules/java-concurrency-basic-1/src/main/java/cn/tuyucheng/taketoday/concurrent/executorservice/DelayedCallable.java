package cn.tuyucheng.taketoday.concurrent.executorservice;


import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class DelayedCallable implements Callable<String> {

   private final String name;
   private final long period;
   private CountDownLatch latch;

   public DelayedCallable(String name, long period, CountDownLatch latch) {
      this(name, period);
      this.latch = latch;
   }

   public DelayedCallable(String name, long period) {
      this.name = name;
      this.period = period;
   }

   public String call() {
      try {
         Thread.sleep(period);
         if (latch != null) {
            latch.countDown();
         }

      } catch (InterruptedException ex) {
         // handle exception
         ex.printStackTrace();
         Thread.currentThread().interrupt();
      }

      return name;
   }
}