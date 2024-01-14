package cn.tuyucheng.taketoday.doubletolong;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DoubleToLongUnitTest {

   final static double VALUE = 9999.999;

   @Test
   public void givenDoubleValue_whenLongValueCalled_thenLongValueReturned() {
      Assertions.assertEquals(9999L, Double.valueOf(VALUE).longValue());
   }

   @Test
   public void givenDoubleValue_whenMathRoundUsed_thenRoundUp() {
      Assertions.assertEquals(10000L, Math.round(VALUE));
   }

   @Test
   public void givenDoubleValue_whenMathRoundUsed_thenRoundDown() {
      Assertions.assertEquals(9999L, Math.round(9999.444));
   }

   @Test
   public void givenDoubleValue_whenMathRoundUsed_thenSameValueReturned() {
      Assertions.assertEquals(9999L, Math.round(9999.0));
   }

   @Test
   public void givenDoubleValue_whenMathCeilUsed_thenLongValueReturned() {
      Assertions.assertEquals(10000L, Math.ceil(VALUE), 0);
   }

   @Test
   public void givenDoubleValue_whenMathCeilUsed_thenSameValueReturned() {
      Assertions.assertEquals(9999L, Math.ceil(9999.0), 0);
   }

   @Test
   public void givenDoubleValue_whenMathCeilUsed_thenDifferentThanRound() {
      Assertions.assertEquals(10000L, Math.ceil(9999.444), 0);
   }

   @Test
   public void givenDoubleValue_whenMathFloorUsed_thenLongValueReturned() {
      Assertions.assertEquals(9999L, Math.floor(VALUE), 0);
   }

   @Test
   public void givenDoubleValue_whenMathFloorUsed_thenSameValueReturned() {
      Assertions.assertEquals(9999L, Math.floor(9999.0), 0);
   }

   @Test
   public void givenDoubleValue_whenMathFloorUsed_thenDifferentThanCeil() {
      Assertions.assertEquals(9999L, Math.floor(9999.444), 0);
   }

   @Test
   public void givenDoubleValue_whenTypeCasted_thenLongValueReturned() {
      Assertions.assertEquals(9999L, (long) VALUE);
   }
}
