package cn.tuyucheng.taketoday.parallel;

import org.junit.jupiter.api.Test;

class FirstParallelUnitTest {

   @Test
   void first() throws Exception {
      System.out.println("FirstParallelUnitTest first() start => " + Thread.currentThread().getName());
      Thread.sleep(500);
      System.out.println("FirstParallelUnitTest first() end => " + Thread.currentThread().getName());
   }

   @Test
   void second() throws Exception {
      System.out.println("FirstParallelUnitTest second() start => " + Thread.currentThread().getName());
      Thread.sleep(500);
      System.out.println("FirstParallelUnitTest second() end => " + Thread.currentThread().getName());
   }
}