package cn.tuyucheng.taketoday.bigdecimaltimesint;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BigDecimalxIntegerUnitTest {
   private static final BigDecimal BIG = new BigDecimal("42.42");
   private static final int INT = 10;
   private static final BigDecimal EXPECTED = new BigDecimal("424.2");

   @Test
   void givenBigDecimalAndInt_whenTimes_thenGetExpectedResult() {
      BigDecimal result = BIG.multiply(BigDecimal.valueOf(INT));

      assertEquals(0, EXPECTED.compareTo(result));
      assertThat(result).isEqualByComparingTo(EXPECTED);
   }
}