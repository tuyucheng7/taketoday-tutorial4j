package cn.tuyucheng.taketoday.entitydtodifferences.controller;

import cn.tuyucheng.taketoday.entitydtodifferences.dto.UserCreationDto;
import cn.tuyucheng.taketoday.entitydtodifferences.dto.UserResponseDto;
import cn.tuyucheng.taketoday.entitydtodifferences.mapper.UserMapper;
import cn.tuyucheng.taketoday.entitydtodifferences.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

   private final UserRepository userRepository;

   public UserController(UserRepository userRepository) {
      this.userRepository = userRepository;
   }

   @GetMapping
   public List<UserResponseDto> getUsers() {
      return userRepository.findAll()
            .stream()
            .map(UserMapper::toDto)
            .collect(Collectors.toList());
   }

   @PostMapping
   public UserResponseDto createUser(@RequestBody UserCreationDto userCreationDto) {
      return UserMapper.toDto(userRepository.save(UserMapper.toEntity(userCreationDto)));
   }
}