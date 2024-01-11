package cn.tuyucheng.taketoday.repository;

import cn.tuyucheng.taketoday.config.MongoConfig;
import cn.tuyucheng.taketoday.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * This test requires:
 * * mongodb instance running on the environment
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MongoConfig.class)
class UserRepositoryProjectionLiveTest {

   @Autowired
   private UserRepository userRepository;

   @Autowired
   private MongoOperations mongoOps;

   @BeforeEach
   void testSetup() {
      if (!mongoOps.collectionExists(User.class)) {
         mongoOps.createCollection(User.class);
      }
   }

   @AfterEach
   void tearDown() {
      mongoOps.dropCollection(User.class);
   }

   @Test
   void givenUserExists_whenAgeZero_thenSuccess() {
      mongoOps.insert(new User("John", 30));
      mongoOps.insert(new User("Ringo", 35));

      userRepository.findNameAndId()
            .forEach(user -> {
               assertNotNull(user.getName());
               assertEquals(0, (int) user.getAge());
            });
   }

   @Test
   void givenUserExists_whenIdNull_thenSuccess() {
      mongoOps.insert(new User("John", 30));
      mongoOps.insert(new User("Ringo", 35));

      userRepository.findNameAndAgeExcludeId()
            .forEach(user -> {
               assertNull(user.getId());
               assertNotNull(user.getAge());
            });
   }

}
