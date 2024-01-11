package cn.tuyucheng.taketoday.mongotemplate;

import cn.tuyucheng.taketoday.config.SimpleMongoConfig;
import cn.tuyucheng.taketoday.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This test requires:
 * * mongodb instance running on the environment
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SimpleMongoConfig.class)
public class MongoTemplateProjectionLiveTest {

   @Autowired
   private MongoTemplate mongoTemplate;

   @BeforeEach
   public void testSetup() {
      if (!mongoTemplate.collectionExists(User.class)) {
         mongoTemplate.createCollection(User.class);
      }
   }

   @AfterEach
   public void tearDown() {
      mongoTemplate.dropCollection(User.class);
   }

   @Test
   public void givenUserExists_whenAgeZero_thenSuccess() {
      mongoTemplate.insert(new User("John", 30));
      mongoTemplate.insert(new User("Ringo", 35));

      final Query query = new Query();
      query.fields()
            .include("name");

      mongoTemplate.find(query, User.class)
            .forEach(user -> {
               assertNotNull(user.getName());
               assertTrue(user.getAge()
                     .equals(0));
            });
   }

   @Test
   public void givenUserExists_whenIdNull_thenSuccess() {
      mongoTemplate.insert(new User("John", 30));
      mongoTemplate.insert(new User("Ringo", 35));

      final Query query = new Query();
      query.fields()
            .exclude("_id");

      mongoTemplate.find(query, User.class)
            .forEach(user -> {
               assertNull(user.getId());
               assertNotNull(user.getAge());
            });

   }

}
