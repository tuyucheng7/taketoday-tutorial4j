package cn.tuyucheng.taketoday.spring.cloud.openfeign.config;

import cn.tuyucheng.taketoday.spring.cloud.openfeign.exception.BadRequestException;
import cn.tuyucheng.taketoday.spring.cloud.openfeign.exception.NotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {
   @Override
   public Exception decode(String methodKey, Response response) {
      return switch (response.status()) {
         case 400 -> new BadRequestException();
         case 404 -> new NotFoundException("Not found !!!");
         default -> new Exception("Generic error");
      };
   }
}