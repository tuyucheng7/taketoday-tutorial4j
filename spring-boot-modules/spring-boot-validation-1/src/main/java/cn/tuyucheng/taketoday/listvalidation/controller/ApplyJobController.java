package cn.tuyucheng.taketoday.listvalidation.controller;

import cn.tuyucheng.taketoday.listvalidation.domain.JobAspirant;
import cn.tuyucheng.taketoday.listvalidation.groups.AllLevels;
import cn.tuyucheng.taketoday.listvalidation.groups.Junior;
import cn.tuyucheng.taketoday.listvalidation.groups.MidSenior;
import cn.tuyucheng.taketoday.listvalidation.groups.Senior;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ApplyJobController {

   @PostMapping("/applyLevelJunior")
   public ResponseEntity<String> applyLevelJunior(@Validated({Junior.class, AllLevels.class}) @RequestBody JobAspirant user) {
      return ResponseEntity.ok("Application submitted successfully");
   }

   @PostMapping("/applyLevelMidSenior")
   public ResponseEntity<String> applyLevelMidSenior(@Validated({MidSenior.class, AllLevels.class}) @RequestBody JobAspirant user) {
      return ResponseEntity.ok("Application submitted successfully");
   }

   @PostMapping("/applyLevelSenior")
   public ResponseEntity<String> applyLevelSenior(@Validated({Senior.class, AllLevels.class}) @RequestBody JobAspirant user) {
      return ResponseEntity.ok("Application submitted successfully");
   }

   @ResponseStatus(HttpStatus.BAD_REQUEST)
   @ExceptionHandler(MethodArgumentNotValidException.class)
   public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
      Map<String, String> errors = new HashMap<>();
      ex.getBindingResult().getAllErrors().forEach((error) -> {
         String fieldName = ((FieldError) error).getField();
         String errorMessage = error.getDefaultMessage();
         errors.put(fieldName, errorMessage);
      });
      return errors;
   }
}