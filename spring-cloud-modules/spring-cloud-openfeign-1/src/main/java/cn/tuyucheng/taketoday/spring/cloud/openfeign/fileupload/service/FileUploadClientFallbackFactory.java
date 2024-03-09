package cn.tuyucheng.taketoday.spring.cloud.openfeign.fileupload.service;

import cn.tuyucheng.taketoday.spring.cloud.openfeign.exception.BadRequestException;
import cn.tuyucheng.taketoday.spring.cloud.openfeign.exception.NotFoundException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class FileUploadClientFallbackFactory implements FallbackFactory<FileUploadClientWithFallbackFactory> {
   @Override
   public FileUploadClientWithFallbackFactory create(Throwable cause) {
      return file -> {
         if (cause instanceof BadRequestException) {
            return "Bad Request!!!";
         }
         if (cause instanceof NotFoundException) {
            return "Not Found!!!";
         }
         if (cause instanceof Exception) {
            return "Exception!!!";
         }
         return "Successfully Uploaded file!!!";
      };
   }
}