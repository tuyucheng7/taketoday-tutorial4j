package cn.tuyucheng.taketoday.javadoublevsbigdecimal;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class BigDecimalConversionUnitTest {

   @Test
   void whenConvertingDoubleToBigDecimal_thenConversionIsCorrect() {
      double doubleValue = 123.456;
      BigDecimal bigDecimalValue = BigDecimal.valueOf(doubleValue);
      BigDecimal expected = new BigDecimal("123.456").setScale(3, RoundingMode.HALF_UP);
      assertEquals(expected, bigDecimalValue.setScale(3, RoundingMode.HALF_UP));
   }

   @Test
   void givenDecimalPlacesGreaterThan15_whenConvertingBigDecimalToDouble_thenPrecisionIsLost() {
      BigDecimal bigDecimalValue = new BigDecimal("789.1234567890123456");
      double doubleValue = bigDecimalValue.doubleValue();
      BigDecimal convertedBackToBigDecimal = BigDecimal.valueOf(doubleValue);
      assertNotEquals(bigDecimalValue, convertedBackToBigDecimal);
   }
}
