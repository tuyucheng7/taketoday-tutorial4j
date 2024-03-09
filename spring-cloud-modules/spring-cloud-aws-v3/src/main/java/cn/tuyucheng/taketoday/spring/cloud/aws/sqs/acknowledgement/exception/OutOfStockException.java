package cn.tuyucheng.taketoday.spring.cloud.aws.sqs.acknowledgement.exception;

public class OutOfStockException extends RuntimeException {

   public OutOfStockException(String errorMessage) {
      super(errorMessage);
   }
}