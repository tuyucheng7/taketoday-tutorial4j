package cn.tuyucheng.taketoday.streams.intarraytostrings;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static cn.tuyucheng.taketoday.streams.intarraytostrings.ArrayConversionUtils.convertToString;
import static cn.tuyucheng.taketoday.streams.intarraytostrings.ArrayConversionUtils.convertToStringArray;
import static cn.tuyucheng.taketoday.streams.intarraytostrings.ArrayConversionUtils.convertToStringArrayWithBoxing;

public class IntArrayToStringUnitTest {

   @Test
   public void whenConvertingIntegers_thenHandleStreamOfIntegers() {
      Integer[] integerNumbers = {1, 2, 3, 4, 5};
      String[] expectedOutput = {"1", "2", "3", "4", "5"};

      String[] strings = convertToStringArray(integerNumbers);

      Assertions.assertArrayEquals(expectedOutput, strings);
   }

   @Test
   public void whenConvertingInts_thenHandleIntStream() {
      int[] intNumbers = {1, 2, 3, 4, 5};
      String[] expectedOutput = {"1", "2", "3", "4", "5"};

      String[] strings = convertToStringArray(intNumbers);

      Assertions.assertArrayEquals(expectedOutput, strings);
   }

   @Test
   public void givenAnIntArray_whenBoxingToInteger_thenHandleStreamOfIntegers() {
      int[] intNumbers = {1, 2, 3, 4, 5};
      String[] expectedOutput = {"1", "2", "3", "4", "5"};

      String[] strings = convertToStringArrayWithBoxing(intNumbers);

      Assertions.assertArrayEquals(expectedOutput, strings);
   }

   @Test
   public void givenAnIntArray_whenUsingCollectorsJoining_thenReturnCommaSeparatedString() {
      int[] intNumbers = {1, 2, 3, 4, 5};
      String expectedOutput = "1, 2, 3, 4, 5";

      String string = convertToString(intNumbers);

      Assertions.assertEquals(expectedOutput, string);
   }

}
