package cn.tuyucheng.taketoday.repository;

import cn.tuyucheng.taketoday.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;

/**
 * This test requires:
 * * mongodb instance running on the environment
 */
class BaseQueryLiveTest {

   @Autowired
   protected UserRepository userRepository;

   @Autowired
   protected MongoOperations mongoOps;

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
}