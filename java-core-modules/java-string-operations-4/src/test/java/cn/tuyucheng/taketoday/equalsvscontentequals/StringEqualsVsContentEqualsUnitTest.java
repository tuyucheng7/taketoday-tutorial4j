package cn.tuyucheng.taketoday.equalsvscontentequals;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
