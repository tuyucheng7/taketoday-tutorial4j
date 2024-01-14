package cn.tuyucheng.taketoday.interview;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringFormatUnitTest {
   @Test
   public void givenString_whenUsingStringFormat_thenStringFormatted() {
      String title = "Tuyucheng";
      String formatted = String.format("Title is %s", title);
      assertEquals(formatted, "Title is Tuyucheng");
   }
}
