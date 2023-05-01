package cn.tuyucheng.taketoday.problem.web;

import cn.tuyucheng.taketoday.problem.error.RecordNotFoundException;
import cn.tuyucheng.taketoday.problem.model.Employee;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping(path = "/employees")
public class EmployeeController {

   @Value("${hostname}")
   private String hostname;

   @GetMapping("/v1/{id}")
   public ResponseEntity getEmployeeByIdV1(@PathVariable("id") Long id) {
      if (id < 100)
         return ResponseEntity.ok(new Employee(id, "John", "Doe", "admin@gmail.com"));
      else
         throw new RecordNotFoundException("Employee id '" + id + "' does not exist");
   }

   @GetMapping("/v2/{id}")
   public ResponseEntity getEmployeeByIdV2(@PathVariable("id") Long id) {
      if (id < 100)
         return ResponseEntity.ok(new Employee(id, "John", "Doe", "admin@gmail.com"));
      else {
         ProblemDetail pd = ProblemDetail
               .forStatusAndDetail(HttpStatus.NOT_FOUND, "Employee id '" + id + "' does not exist");
         pd.setType(URI.create("https://my-app-host.com/errors/not-found"));
         pd.setTitle("Record Not Found");
         pd.setProperty("hostname", hostname);
         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(pd);
      }
   }

   @GetMapping("/v3/{id}")
   public ResponseEntity getEmployeeByIdV3(@PathVariable("id") Long id) {
      try {
         throw new NullPointerException("Something was expected but it was null");
      } catch (NullPointerException npe) {
         ProblemDetail pd = ProblemDetail
               .forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Null Pointer Exception");
         pd.setType(URI.create("https://my-app-host.com/errors/npe"));
         pd.setTitle("Null Pointer Exception");
         pd.setProperty("hostname", hostname);
         throw new ErrorResponseException(HttpStatus.NOT_FOUND, pd, npe);
      }
   }
}