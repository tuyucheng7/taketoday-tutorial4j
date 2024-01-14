package cn.tuyucheng.taketoday.arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MaxArrySizeUnitTest {

   @Test
   public void whenInitialArrayMoreThanMaxSize_thenThrowArray() {
      boolean initalized = false;
      try {
         int[] arr = new int[Integer.MAX_VALUE - 1];
         initalized = true;
      } catch (Throwable e) {
         Assertions.assertTrue(e.getMessage().contains("Requested array size exceeds VM limit"));
      }
      Assertions.assertFalse(initalized);
   }

   @Test
   public void whenInitialArrayLessThanMaxSize_thenThrowArray() {
      int[] arr = null;
      try {
         arr = new int[Integer.MAX_VALUE - 2];
      } catch (Throwable e) {
         Assertions.assertTrue(e.getMessage().contains("Java heap space"));
      }
   }

}
