package cn.tuyucheng.taketoday;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringFunctionUnitTest {

   @Test
   public void test_upperCase() {
      assertEquals("TESTCASE", "testCase".toUpperCase());
   }

   @Test
   public void test_indexOf() {
      assertEquals(1, "testCase".indexOf("e"));
   }
}
