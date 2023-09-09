package cn.tuyucheng.taketoday.junit5.nonstatic;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BeforeAndAfterAnnotationsUnitTest {

   String input;
   Long result;

   @BeforeAll
   void setup() {
      input = "77";
   }

   @AfterAll
   void teardown() {
      input = null;
      result = null;
   }

   @Test
   void whenConvertStringToLong_thenResultShouldBeLong() {
      result = Long.valueOf(input);
      assertEquals(77L, result);
   }
}