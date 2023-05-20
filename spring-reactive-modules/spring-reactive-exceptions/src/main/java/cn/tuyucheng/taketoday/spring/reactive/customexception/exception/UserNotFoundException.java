package cn.tuyucheng.taketoday.spring.reactive.customexception.exception;

public class UserNotFoundException extends RuntimeException {

   public UserNotFoundException(String message) {
      super(message);
   }
}