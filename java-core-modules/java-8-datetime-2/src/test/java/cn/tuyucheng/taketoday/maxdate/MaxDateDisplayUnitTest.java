package cn.tuyucheng.taketoday.maxdate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaxDateDisplayUnitTest {

   @Test
   void whenGetMaxDate_thenCorrectResult() {
      MaxDateDisplay display = new MaxDateDisplay();
      String result = display.getMaxDateValue();
      assertEquals(
            "The maximum date value in Java is: 292278994-08-17 07:12:55.807",
            result
      );
   }
}
