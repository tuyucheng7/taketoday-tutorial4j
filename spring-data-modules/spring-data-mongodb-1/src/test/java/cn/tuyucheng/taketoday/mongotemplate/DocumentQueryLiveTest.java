package cn.tuyucheng.taketoday.mongotemplate;

import cn.tuyucheng.taketoday.config.MongoConfig;
import cn.tuyucheng.taketoday.model.EmailAddress;
import cn.tuyucheng.taketoday.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertThat;

/**
 * This test requires:
 * * mongodb instance running on the environment
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MongoConfig.class)
public class DocumentQueryLiveTest {

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
      mongoTemplate.dropCollection(EmailAddress.class);
      mongoTemplate.dropCollection(User.class);
   }

   @Test
   public void givenUsersExist_whenFindingUsersByName_thenUsersAreFound() {
      User user = new User();
      user.setName("Eric");
      user.setAge(45);
      mongoTemplate.insert(user);
      user = new User();
      user.setName("Antony");
      user.setAge(55);
      mongoTemplate.insert(user);

      Query query = new Query();
      query.addCriteria(Criteria.where("name").is("Eric"));
      List<User> users = mongoTemplate.find(query, User.class);

      assertThat(users.size(), is(1));
   }

   @Test
   public void givenUsersExist_whenFindingUserWithAgeLessThan50AndGreateThan20_thenUsersAreFound() {
      User user = new User();
      user.setAge(20);
      user.setName("Jon");
      mongoTemplate.insert(user);

      user = new User();
      user.setAge(50);
      user.setName("Jon");
      mongoTemplate.insert(user);

      user = new User();
      user.setAge(33);
      user.setName("Jim");
      mongoTemplate.insert(user);

      Query query = new Query();
      query.addCriteria(Criteria.where("age").lt(40).gt(26));
      List<User> users = mongoTemplate.find(query, User.class);

      assertThat(users.size(), is(1));
   }

   @Test
   public void givenUsersExist_whenFindingUserWithNameStartWithA_thenUsersAreFound() {
      User user = new User();
      user.setName("Eric");
      user.setAge(45);
      mongoTemplate.insert(user);

      user = new User();
      user.setName("Antony");
      user.setAge(33);
      mongoTemplate.insert(user);

      user = new User();
      user.setName("Alice");
      user.setAge(35);
      mongoTemplate.insert(user);

      Query query = new Query();
      query.addCriteria(Criteria.where("name").regex("^A"));

      List<User> users = mongoTemplate.find(query, User.class);

      assertThat(users.size(), is(2));
   }

   @Test
   public void givenUsersExist_whenFindingUserWithNameEndWithC_thenUsersAreFound() {
      User user = new User();
      user.setName("Eric");
      user.setAge(45);
      mongoTemplate.insert(user);

      user = new User();
      user.setName("Antony");
      user.setAge(33);
      mongoTemplate.insert(user);

      user = new User();
      user.setName("Alice");
      user.setAge(35);
      mongoTemplate.insert(user);

      Query query = new Query();
      query.addCriteria(Criteria.where("name").regex("c$"));

      List<User> users = mongoTemplate.find(query, User.class);

      assertThat(users.size(), is(1));
   }

   @Test
   public void givenUsersExist_whenFindingByPage_thenUsersAreFoundByPage() {
      User user = new User();
      user.setName("Eric");
      user.setAge(45);
      mongoTemplate.insert(user);

      user = new User();
      user.setName("Antony");
      user.setAge(33);
      mongoTemplate.insert(user);

      user = new User();
      user.setName("Alice");
      user.setAge(35);
      mongoTemplate.insert(user);

      final Pageable pageableRequest = PageRequest.of(0, 2);
      Query query = new Query();
      query.with(pageableRequest);

      List<User> users = mongoTemplate.find(query, User.class);

      assertThat(users.size(), is(2));
   }

   @Test
   public void givenUsersExist_whenFindingUsersAndSortThem_thenUsersAreFoundAndSorted() {
      User user = new User();
      user.setName("Eric");
      user.setAge(45);
      mongoTemplate.insert(user);

      user = new User();
      user.setName("Antony");
      user.setAge(33);
      mongoTemplate.insert(user);

      user = new User();
      user.setName("Alice");
      user.setAge(35);
      mongoTemplate.insert(user);

      Query query = new Query();
      query.with(Sort.by(Direction.ASC, "age"));

      List<User> users = mongoTemplate.find(query, User.class);

      Iterator<User> iter = users.iterator();
      assertThat(users.size(), is(3));
      assertThat(iter.next().getName(), is("Antony"));
      assertThat(iter.next().getName(), is("Alice"));
      assertThat(iter.next().getName(), is("Eric"));
   }
}
