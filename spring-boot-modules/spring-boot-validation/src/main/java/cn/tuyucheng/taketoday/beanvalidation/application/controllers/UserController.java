package cn.tuyucheng.taketoday.beanvalidation.application.controllers;

import cn.tuyucheng.taketoday.beanvalidation.application.entities.User;
import cn.tuyucheng.taketoday.beanvalidation.application.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

   private final UserRepository userRepository;

   @Autowired
   public UserController(UserRepository userRepository) {
      this.userRepository = userRepository;
   }

   @GetMapping("/users")
   public List<User> getUsers() {
      return (List<User>) userRepository.findAll();
   }

   @PostMapping("/users")
   ResponseEntity<String> addUser(@Valid @RequestBody User user) {
      return ResponseEntity.ok("User is valid");
   }

   @ResponseStatus(HttpStatus.BAD_REQUEST)
   @ExceptionHandler(MethodArgumentNotValidException.class)
   public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
      Map<String, String> errors = new HashMap<>();
      ex.getBindingResult().getAllErrors().forEach(error -> {
         String fieldName = ((FieldError) error).getField();
         String errorMessage = error.getDefaultMessage();
         errors.put(fieldName, errorMessage);
      });
      return errors;
   }
}