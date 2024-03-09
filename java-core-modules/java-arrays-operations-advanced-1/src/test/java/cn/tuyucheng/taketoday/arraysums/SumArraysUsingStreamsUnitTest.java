package cn.tuyucheng.taketoday.arraysums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class SumArraysUsingStreamsUnitTest {

   @Test
   public void sumOfTwoArraysUsingStreams_GivenTwoEqualSizedIntArrays_ReturnsCorrectSumArray() {
      int[] arr1 = {4, 5, 1, 6, 4, 15};
      int[] arr2 = {3, 5, 6, 1, 9, 6};
      int[] expected = {7, 10, 7, 7, 13, 21};
      assertArrayEquals(expected, SumArraysUsingStreams.sumOfTwoArrays(arr1, arr2));
   }
}
