package cn.tuyucheng.taketoday.algorithms.gradientdescent;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GradientDescentUnitTest {

   @Test
   void givenFunction_whenStartingPointIsOne_thenLocalMinimumIsFound() {
      Function<Double, Double> df = x ->
            StrictMath.abs(StrictMath.pow(x, 3)) - (3 * StrictMath.pow(x, 2)) + x;
      GradientDescent gd = new GradientDescent();
      double res = gd.findLocalMinimum(df, 1);
      assertTrue(res > 1.78);
      assertTrue(res < 1.84);
   }
}
