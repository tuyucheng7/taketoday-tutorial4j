package cn.tuyucheng.taketoday.transaction;

import cn.tuyucheng.taketoday.config.MongoConfig;
import cn.tuyucheng.taketoday.model.User;
import cn.tuyucheng.taketoday.repository.UserRepository;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoTransactionException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This test requires:
 * * mongodb instance running on the environment
 * Run the src/live-test/resources/live-test-setup.sh
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MongoConfig.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MongoTransactionalLiveTest {

   @Autowired
   private MongoTemplate mongoTemplate;

   @Autowired
   private UserRepository userRepository;

   @Test
   @Transactional
   void whenPerformMongoTransaction_thenSuccess() {
      userRepository.save(new User("John", 30));
      userRepository.save(new User("Ringo", 35));
      Query query = new Query().addCriteria(Criteria.where("name")
            .is("John"));
      List<User> users = mongoTemplate.find(query, User.class);

      assertThat(users.size(), is(1));
   }

   @Test
   @Transactional
   void whenListCollectionDuringMongoTransaction_thenException() {
      if (mongoTemplate.collectionExists(User.class)) {
         mongoTemplate.save(new User("John", 30));
         assertThrows(MongoTransactionException.class, () -> mongoTemplate.save(new User("Ringo", 35)));
      }
   }

   // ==== Using test instead of before and after due to @transactional doesn't allow list collection

   @Test
   void setup() {
      if (!mongoTemplate.collectionExists(User.class)) {
         mongoTemplate.createCollection(User.class);
      }
   }

   @Test
   void ztearDown() {
      mongoTemplate.dropCollection(User.class);
   }
}