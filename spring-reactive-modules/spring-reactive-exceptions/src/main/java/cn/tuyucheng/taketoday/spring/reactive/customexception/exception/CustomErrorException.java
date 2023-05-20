package cn.tuyucheng.taketoday.spring.reactive.customexception.exception;

import cn.tuyucheng.taketoday.spring.reactive.customexception.model.CustomErrorResponse;
import lombok.Getter;

public class CustomErrorException extends RuntimeException {
   @Getter
   private CustomErrorResponse errorResponse;

   public CustomErrorException(String message, CustomErrorResponse errorResponse) {
      super(message);
      this.errorResponse = errorResponse;
   }
}