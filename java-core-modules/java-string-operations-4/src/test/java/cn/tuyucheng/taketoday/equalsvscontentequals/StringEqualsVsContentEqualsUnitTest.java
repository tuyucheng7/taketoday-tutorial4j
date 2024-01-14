package cn.tuyucheng.taketoday.equalsvscontentequals;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StringEqualsVsContentEqualsUnitTest {

   String actualString = "tuyucheng";
   String identicalString = "tuyucheng";
   CharSequence identicalStringInstance = "tuyucheng";
   CharSequence identicalStringBufferInstance = new StringBuffer("tuyucheng");

   @Test
   public void whenIdenticalTestString_thenBothTrue() {
      assertTrue(actualString.equals(identicalString));
      assertTrue(actualString.contentEquals(identicalString));
   }

   @Test
   public void whenSameContentButDifferentType_thenEqualsIsFalseAndContentEqualsIsTrue() {
      assertTrue(actualString.equals(identicalStringInstance));
      assertTrue(actualString.contentEquals(identicalStringInstance));

      assertFalse(actualString.equals(identicalStringBufferInstance));
      assertTrue(actualString.contentEquals(identicalStringBufferInstance));
   }

}
