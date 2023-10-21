package cn.tuyucheng.taketoday.exceptions.throwvsthrows;

public class NullOrEmptyException extends RuntimeException {

   public NullOrEmptyException(String errorMessage) {
      super(errorMessage);
   }
}