package cn.tuyucheng.taketoday.junit;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringCaseUnitTest {

   private static String data;

   @BeforeClass
   public static void setup() {
      data = "HELLO TUYUCHENG";
   }

   @Test
   public void givenString_whenAllCaps_thenCorrect() {
      assertEquals(data.toUpperCase(), data);
   }
}