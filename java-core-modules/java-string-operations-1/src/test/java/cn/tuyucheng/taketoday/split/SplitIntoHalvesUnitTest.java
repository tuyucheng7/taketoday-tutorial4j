package cn.tuyucheng.taketoday.split;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SplitIntoHalvesUnitTest {

   @Test
   public void givenAString_whenSplitInHalf_thenCorrectParts() {
      String hello = "Tuyucheng";
      int mid = hello.length() / 2;
      String[] parts = {hello.substring(0, mid), hello.substring(mid)};

      assertEquals("Bael", parts[0]);
      assertEquals("dung", parts[1]);
   }
}
