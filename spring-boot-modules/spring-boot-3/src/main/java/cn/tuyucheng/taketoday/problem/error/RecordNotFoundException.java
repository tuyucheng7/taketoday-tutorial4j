package cn.tuyucheng.taketoday.problem.error;

public class RecordNotFoundException extends RuntimeException {

   private final String message;

   public RecordNotFoundException(String message) {
      this.message = message;
   }
}