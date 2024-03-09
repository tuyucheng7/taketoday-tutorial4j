package cn.tuyucheng.taketoday.spring.data.persistence.findvsget.service;

import cn.tuyucheng.taketoday.spring.data.persistence.findvsget.entity.User;
import cn.tuyucheng.taketoday.spring.data.persistence.findvsget.repository.SimpleUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SimpleUserService {

   private static final Logger log = LoggerFactory.getLogger(SimpleUserService.class);
   private final SimpleUserRepository repository;

   public SimpleUserService(final SimpleUserRepository repository) {
      this.repository = repository;
   }

   public User findUser(final long id) {
      log.info("Before requesting a user in a findUser method");
      final Optional<User> optionalUser = repository.findById(id);
      log.info("After requesting a user in a findUser method");
      final User user = optionalUser.orElse(null);
      log.info("After unwrapping an optional in a findUser method");
      return user;
   }
}