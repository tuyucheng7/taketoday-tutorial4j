package cn.tuyucheng.taketoday.spring.cloud.aws.sqs.acknowledgement.exception;

public class ProductNotFoundException extends RuntimeException {

   public ProductNotFoundException(String errorMessage) {
      super(errorMessage);
   }
}