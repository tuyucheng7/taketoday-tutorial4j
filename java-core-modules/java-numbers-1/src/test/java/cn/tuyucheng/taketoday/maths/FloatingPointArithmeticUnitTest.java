package cn.tuyucheng.taketoday.maths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class FloatingPointArithmeticUnitTest {

   @Test
   public void givenDecimalNumbers_whenAddedTogether_thenGetExpectedResult() {
      double a = 13.22;
      double b = 4.88;
      double c = 21.45;
      double result = 39.55;

      double abc = a + b + c;
      double acb = a + c + b;

      Assertions.assertEquals(result, abc, 0);
      Assertions.assertNotEquals(result, acb, 0);

      double ab = 18.1;
      double ac = 34.67;

      double ab_c = ab + c;
      double ac_b = ac + b;

      Assertions.assertEquals(result, ab_c, 0);
      Assertions.assertNotEquals(result, ac_b, 0);

      BigDecimal d = new BigDecimal(String.valueOf(a));
      BigDecimal e = new BigDecimal(String.valueOf(b));
      BigDecimal f = new BigDecimal(String.valueOf(c));
      BigDecimal sum = new BigDecimal("39.55");

      BigDecimal def = d.add(e).add(f);
      BigDecimal dfe = d.add(f).add(e);

      Assertions.assertEquals(0, def.compareTo(sum));
      Assertions.assertEquals(0, dfe.compareTo(sum));

      Assertions.assertNotEquals(0, sum.compareTo(new BigDecimal(String.valueOf(acb))));
   }
}
