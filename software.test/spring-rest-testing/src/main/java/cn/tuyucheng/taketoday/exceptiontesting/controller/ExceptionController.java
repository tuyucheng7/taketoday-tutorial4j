package cn.tuyucheng.taketoday.exceptiontesting.controller;

import cn.tuyucheng.taketoday.exceptiontesting.exception.BadArgumentsException;
import cn.tuyucheng.taketoday.exceptiontesting.exception.InternalException;
import cn.tuyucheng.taketoday.exceptiontesting.exception.ResourceNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExceptionController {

   @GetMapping("/exception/{exception_id}")
   public void getSpecificException(@PathVariable("exception_id") String pException) {
      if ("not_found".equals(pException)) {
         throw new ResourceNotFoundException("resource not found");
      } else if ("bad_arguments".equals(pException)) {
         throw new BadArgumentsException("bad arguments");
      } else {
         throw new InternalException("internal error");
      }
   }

   @GetMapping("/exception/throw")
   public void getException() throws Exception {
      throw new Exception("error");
   }
}