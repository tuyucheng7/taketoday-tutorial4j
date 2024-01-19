package cn.tuyucheng.taketoday.spring.cloud.openfeign.customizederrorhandling.config;


import cn.tuyucheng.taketoday.spring.cloud.openfeign.customizederrorhandling.exception.ProductNotFoundException;
import cn.tuyucheng.taketoday.spring.cloud.openfeign.customizederrorhandling.exception.ProductServiceNotAvailableException;
import cn.tuyucheng.taketoday.spring.cloud.openfeign.exception.BadRequestException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {

   @Override
   public Exception decode(String methodKey, Response response) {
      return switch (response.status()) {
         case 400 -> new BadRequestException();
         case 404 -> new ProductNotFoundException("Product not found");
         case 503 -> new ProductServiceNotAvailableException("Product Api is unavailable");
         default -> new Exception("Exception while getting product details");
      };
   }
}