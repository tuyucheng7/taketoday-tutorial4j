package cn.tuyucheng.taketoday.startdbwithawssecretsmanager.controller;

import cn.tuyucheng.taketoday.startdbwithawssecretsmanager.model.UserEntity;
import cn.tuyucheng.taketoday.startdbwithawssecretsmanager.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserController {

   private final UserRepository userRepository;

   public UserController(UserRepository userRepository) {
      this.userRepository = userRepository;
   }

   @GetMapping(value = "/{id}", produces = "application/json")
   public @ResponseBody UserEntity getUser(@PathVariable Long id) {
      return userRepository.findById(id).get();
   }

   @PostMapping
   public UserEntity createUser(@RequestBody UserEntity userEntity) {
      return userRepository.save(userEntity);
   }

   @DeleteMapping(value = "/{id}")
   public void removeUser(@PathVariable Long id) {
      userRepository.deleteById(id);
   }
}