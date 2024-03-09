package cn.tuyucheng.taketoday.errorhandling;

public class CustomRequestAuthException extends RuntimeException {
   public CustomRequestAuthException(String message) {
      super(message);
   }
}