package cn.tuyucheng.taketoday;

public class MathUtil {

   public static int add(int num1, int num2) {
      return num1 + num2;
   }

   public static int multiple(int num1, int num2) {
      return num1 * num2;
   }

   public static boolean isEven(int num) {
      return num % 2 == 0;
   }

   public static int division(int num, int by) {
      return num / by;
   }

   public static boolean isPrime(int num) {
      for (int i = 2; 2 * i < num; i++) {
         if (num % i == 0)
            return false;
      }
      return true;
   }
}