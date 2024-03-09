package cn.tuyucheng.taketoday.spring.servicevalidation.controller;

import cn.tuyucheng.taketoday.spring.servicevalidation.domain.UserAccount;
import cn.tuyucheng.taketoday.spring.servicevalidation.service.UserAccountService;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserAccountController {

   @Autowired
   private UserAccountService service;

   @PostMapping("/addUserAccount")
   public ResponseEntity<?> addUserAccount(@RequestBody UserAccount userAccount) {
      try {
         return ResponseEntity.ok(service.addUserAccount(userAccount));
      } catch (ConstraintViolationException e) {
         return ResponseEntity.badRequest().body(e.getMessage());
      }
   }
}