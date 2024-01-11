package cn.tuyucheng.taketoday.transaction;

import cn.tuyucheng.taketoday.config.MongoReactiveConfig;
import cn.tuyucheng.taketoday.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * This test requires:
 * * mongodb instance running on the environment
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MongoReactiveConfig.class)
class MongoTransactionReactiveLiveTest {

   @Autowired
   private ReactiveMongoOperations reactiveOps;

   @BeforeEach
   void testSetup() {
      if (!reactiveOps.collectionExists(User.class)
            .block()) {
         reactiveOps.createCollection(User.class);
      }
   }

   @AfterEach
   void tearDown() {
      System.out.println(reactiveOps.findAll(User.class)
            .count()
            .block());
      reactiveOps.dropCollection(User.class);
   }

   @Test
   void whenPerformTransaction_thenSuccess() {
      User user1 = new User("Jane", 23);
      User user2 = new User("John", 34);
      reactiveOps.inTransaction()
            .execute(action -> action.insert(user1)
                  .then(action.insert(user2)));
   }

}
