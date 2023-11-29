package cn.tuyucheng.taketoday.parallel;

import org.junit.jupiter.api.Test;

class SecondParallelUnitTest {

   @Test
   void first() throws Exception {
      System.out.println("SecondParallelUnitTest first() start => " + Thread.currentThread().getName());
      Thread.sleep(500);
      System.out.println("SecondParallelUnitTest first() end => " + Thread.currentThread().getName());
   }

   @Test
   void second() throws Exception {
      System.out.println("SecondParallelUnitTest second() start => " + Thread.currentThread().getName());
      Thread.sleep(500);
      System.out.println("SecondParallelUnitTest second() end => " + Thread.currentThread().getName());
   }
}