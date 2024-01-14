package cn.tuyucheng.taketoday.concurrent.stopexecution;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FixedTimeTaskUnitTest {

   @Test
   public void run() throws InterruptedException {
      long start = System.currentTimeMillis();
      Thread thread = new Thread(new FixedTimeTask(10));
      thread.start();
      thread.join();
      long end = System.currentTimeMillis();
      assertTrue(end - start >= 10);
   }
}