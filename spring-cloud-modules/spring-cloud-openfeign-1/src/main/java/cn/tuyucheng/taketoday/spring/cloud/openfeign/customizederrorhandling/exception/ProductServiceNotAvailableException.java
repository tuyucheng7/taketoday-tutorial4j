package cn.tuyucheng.taketoday.spring.cloud.openfeign.customizederrorhandling.exception;

public class ProductServiceNotAvailableException extends RuntimeException {

   public ProductServiceNotAvailableException(String message) {
      super(message);
   }
}