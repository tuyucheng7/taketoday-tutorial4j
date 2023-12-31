package cn.tuyucheng.taketoday.roles.rolesauthorities.persistence;

import cn.tuyucheng.taketoday.roles.rolesauthorities.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService implements IUserService {

   @Autowired
   private UserRepository repository;

   public User findUserByEmail(String email) {
      return repository.findByEmail(email);
   }
}