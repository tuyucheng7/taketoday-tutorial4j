package cn.tuyucheng.taketoday.array.conversions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static cn.tuyucheng.taketoday.array.conversions.IntArrayToIterableConversionUtils.convertWithApacheCommonsAndJavaAsList;
import static cn.tuyucheng.taketoday.array.conversions.IntArrayToIterableConversionUtils.convertWithGuava;
import static cn.tuyucheng.taketoday.array.conversions.IntArrayToIterableConversionUtils.convertWithStreamIterator;
import static cn.tuyucheng.taketoday.array.conversions.IntArrayToIterableConversionUtils.convertWithStreamToList;


class IntArrayToIterableConversionUtilsUnitTest {

   private int[] ints = new int[]{1, 2, 3, 4, 5};

   @Test
   void whenConvertWithStreamToList_thenGetIterable() {
      Iterable<Integer> integers = convertWithStreamToList(ints);
      Assertions.assertTrue(integers instanceof Iterable, "should be Iterable");
   }

   @Test
   void whenConvertWithStreamIterator_thenGetIterable() {
      Iterable<Integer> integers = convertWithStreamIterator(ints);
      Assertions.assertTrue(integers instanceof Iterable, "should be Iterable");
   }

   @Test
   void whenConvertWithApacheCommonsAndJavaAsList_thenGetIterable() {
      Iterable<Integer> integers = convertWithApacheCommonsAndJavaAsList(ints);
      Assertions.assertTrue(integers instanceof Iterable, "should be Iterable");
   }

   @Test
   void whenConvertWithGuava_thenGetIterable() {
      Iterable<Integer> integers = convertWithGuava(ints);
      Assertions.assertTrue(integers instanceof Iterable, "should be Iterable");
   }
}