package cn.tuyucheng.taketoday.interview;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringReverseUnitTest {
   @Test
   public void whenUsingInbuildMethods_thenStringReversed() {
      String reversed = new StringBuilder("tuyucheng").reverse().toString();
      assertEquals("gnudleab", reversed);
   }
}
