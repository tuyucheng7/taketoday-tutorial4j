package cn.tuyucheng.taketoday.junit5vstestng;

public class DivideByZeroException extends RuntimeException {

   public DivideByZeroException(String message) {
      super(message);
   }
}