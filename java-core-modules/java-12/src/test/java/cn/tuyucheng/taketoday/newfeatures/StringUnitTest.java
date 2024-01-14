package cn.tuyucheng.taketoday.newfeatures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringUnitTest {

   @Test
   public void givenString_thenRevertValue() {
      String text = "Tuyucheng";
      String transformed = text.transform(value -> new StringBuilder(value).reverse().toString());
      assertEquals("gnudleaB", transformed);
   }
}
