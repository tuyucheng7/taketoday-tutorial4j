package cn.tuyucheng.taketoday.spring.data.persistence.findvsget;

import cn.tuyucheng.taketoday.spring.data.persistence.findvsget.entity.User;
import cn.tuyucheng.taketoday.spring.data.persistence.findvsget.repository.SimpleUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assumptions.assumeThat;

@SpringBootTest(classes = ApplicationConfig.class, properties = {
      "spring.jpa.generate-ddl=true",
      "spring.jpa.show-sql=false"
})
abstract class DatabaseConfigurationBaseIntegrationTest {

   private static final int NUMBER_OF_USERS = 10;

   @Autowired
   private SimpleUserRepository repository;

   @BeforeEach
   void populateDatabase() {
      final List<User> users = UserProvider.userSource()
            .map(Arguments::get)
            .map(s -> new User(((Long) s[0]), s[1].toString(), s[2].toString()))
            .toList();
      repository.saveAll(users);
      assumeThat(repository.findAll()).hasSize(NUMBER_OF_USERS);
   }

   @AfterEach
   void clearDatabase() {
      repository.deleteAll();
   }
}