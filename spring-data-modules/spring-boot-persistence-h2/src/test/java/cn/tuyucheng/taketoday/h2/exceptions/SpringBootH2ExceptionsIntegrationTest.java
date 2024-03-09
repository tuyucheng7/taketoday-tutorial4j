package cn.tuyucheng.taketoday.h2.exceptions;

import cn.tuyucheng.taketoday.h2.exceptions.models.User;
import cn.tuyucheng.taketoday.h2.exceptions.repos.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringBootH2Exceptions.class)
class SpringBootH2ExceptionsIntegrationTest {

   @Autowired
   private UserRepository userRepository;

   @Test
   void givenValidInitData_whenCallingFindAll_thenReturnData() {
      List<User> users = userRepository.findAll();

      assertThat(users).hasSize(2);
   }
}