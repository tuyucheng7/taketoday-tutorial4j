package cn.tuyucheng.taketoday.system;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SystemNanoUnitTest {

   @Test
   public void givenSystem_whenCalledNanoTime_thenGivesTimeinResult() {
      long startTime = System.nanoTime();
      // do something that takes time
      long endTime = System.nanoTime();

      Assertions.assertTrue(endTime - startTime < 10000);
   }
}
