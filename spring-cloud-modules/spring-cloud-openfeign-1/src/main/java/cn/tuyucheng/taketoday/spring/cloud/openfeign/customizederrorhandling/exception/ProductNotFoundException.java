package cn.tuyucheng.taketoday.spring.cloud.openfeign.customizederrorhandling.exception;

public class ProductNotFoundException extends RuntimeException {

   public ProductNotFoundException(String message) {
      super(message);
   }
}