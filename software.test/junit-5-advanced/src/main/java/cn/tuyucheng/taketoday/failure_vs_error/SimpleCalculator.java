package cn.tuyucheng.taketoday.failure_vs_error;

/**
 * @author tuyucheng
 */
public class SimpleCalculator {

   public static double divideNumbers(double dividend, double divisor) {
      if (divisor == 0) {
         throw new ArithmeticException("Division by zero!");
      }
      return dividend / divisor;
   }
}