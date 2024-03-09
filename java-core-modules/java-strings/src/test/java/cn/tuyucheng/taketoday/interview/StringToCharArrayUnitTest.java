package cn.tuyucheng.taketoday.interview;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringToCharArrayUnitTest {
   @Test
   public void whenConvertingStringToCharArray_thenConversionSuccessful() {
      String beforeConvStr = "hello";
      char[] afterConvCharArr = {'h', 'e', 'l', 'l', 'o'};

      assertEquals(Arrays.equals(beforeConvStr.toCharArray(), afterConvCharArr), true);
   }
}
