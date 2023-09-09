package cn.tuyucheng.taketoday;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("Test case")
public class TaggedUnitTest {

   @Test
   @Tag("Method")
   void testMethod() {
      assertEquals(2 + 2, 4);
   }
}