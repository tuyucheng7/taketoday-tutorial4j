package cn.tuyucheng.taketoday.problem.web;

import cn.tuyucheng.taketoday.problem.error.RecordNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

   @Value("${hostname}")
   private String hostname;

   @ExceptionHandler(RecordNotFoundException.class)
   public ProblemDetail handleRecordNotFoundException(RecordNotFoundException ex, WebRequest request) {
      ProblemDetail body = ProblemDetail.
            forStatusAndDetail(HttpStatusCode.valueOf(404), ex.getLocalizedMessage());

      body.setType(URI.create("https://my-app-host.com/errors/not-found"));
      body.setTitle("Record Not Found");
      body.setProperty("hostname", hostname);

      return body;
   }
}