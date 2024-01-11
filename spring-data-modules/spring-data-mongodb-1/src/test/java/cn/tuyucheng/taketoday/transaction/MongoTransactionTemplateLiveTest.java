package cn.tuyucheng.taketoday.transaction;

import cn.tuyucheng.taketoday.config.MongoConfig;
import cn.tuyucheng.taketoday.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.SessionSynchronization;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This test requires:
 * mongodb instance running on the environment
 * Run the src/live-test/resources/live-test-setup.sh
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MongoConfig.class)
class MongoTransactionTemplateLiveTest {

   @Autowired
   private MongoTemplate mongoTemplate;

   @Autowired
   private MongoTransactionManager mongoTransactionManager;

   @BeforeEach
   void testSetup() {
      if (!mongoTemplate.collectionExists(User.class)) {
         mongoTemplate.createCollection(User.class);
      }
   }

   @AfterEach
   void tearDown() {
      mongoTemplate.dropCollection(User.class);
   }

   @Test
   void givenTransactionTemplate_whenPerformTransaction_thenSuccess() {
      mongoTemplate.setSessionSynchronization(SessionSynchronization.ALWAYS);
      TransactionTemplate transactionTemplate = new TransactionTemplate(mongoTransactionManager);
      transactionTemplate.execute(new TransactionCallbackWithoutResult() {
         @Override
         protected void doInTransactionWithoutResult(TransactionStatus status) {
            mongoTemplate.insert(new User("Kim", 20));
            mongoTemplate.insert(new User("Jack", 45));
         }

         ;
      });

      Query query = new Query().addCriteria(Criteria.where("name")
            .is("Jack"));
      List<User> users = mongoTemplate.find(query, User.class);

      assertThat(users.size(), is(1));
   }
}