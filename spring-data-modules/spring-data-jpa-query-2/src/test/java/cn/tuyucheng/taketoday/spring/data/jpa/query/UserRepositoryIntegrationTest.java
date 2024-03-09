package cn.tuyucheng.taketoday.spring.data.jpa.query;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@DataJpaTest(properties = "spring.sql.init.data-locations=classpath:insert_users.sql", showSql = false)
class UserRepositoryIntegrationTest {

   @Autowired
   private UserRepository userRepository;

   @Test
   void whenFindAllActiveUsersThenAllActiveFound() {
      Collection<User> allActiveUsers = userRepository.findAllActiveUsers();
      assertThat(allActiveUsers).hasSize(2);
   }

   @Test
   void whenFindAllActiveUsersNativeThenAllActiveFound() {
      Collection<User> allActiveUsers = userRepository.findAllActiveUsersNative();
      assertThat(allActiveUsers).hasSize(2);
   }

   @Test
   void whenFindAllSortedByNameThenAllSorted() {
      List<User> allUsersSortedByName = userRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
      assertThat(allUsersSortedByName)
            .extracting("name")
            .containsSequence("Bob", "Cindy", "John");
   }

   @Test
   void whenFindAllSortedByNameLengthThenException() {
      assertThatThrownBy(() -> userRepository.findAll(Sort.by("LENGTH(name)")))
            .isInstanceOf(PropertyReferenceException.class);
   }

   @Test
   void whenFindAllUsersSortedByNameThenAllSorted() {
      List<User> allUsersSortedByName = userRepository.findAllUsers(Sort.by(Sort.Direction.ASC, "name"));
      assertThat(allUsersSortedByName)
            .extracting("name")
            .containsSequence("Bob", "Cindy", "John");
   }

   @Test
   void whenFindAllUsersSortedByNameLengthThenAllSorted() {
      List<User> allUsersSortedByName = userRepository.findAllUsers(JpaSort.unsafe("LENGTH(name)"));
      assertThat(allUsersSortedByName)
            .extracting("name")
            .containsSequence("Bob", "John", "Cindy");
   }

   @Test
   void whenFindAllUsersWithPaginationThenPaginated() {
      Page<User> page = userRepository.findAllUsersWithPagination(PageRequest.of(0, 1));
      assertThat(page.stream().map(User::getId))
            .hasSize(1)
            .containsOnly(1);
   }

   @Test
   void whenFindAllUsersWithPaginationNativeThenPaginated() {
      Page<User> page = userRepository.findAllUsersWithPaginationNative(PageRequest.of(1, 1));
      assertThat(page.stream().map(User::getId))
            .hasSize(1)
            .containsOnly(2);
   }

   @Test
   void whenFindUserByStatusThenFound() {
      User user = userRepository.findUserByStatus(0);
      assertThat(user.getStatus()).isZero();
   }

   @Test
   void whenFindUserByStatusAndNameThenFound() {
      User user = userRepository.findUserByStatusAndName(1, "John");
      assertThat(user.getStatus()).isOne();
      assertThat(user.getName()).isEqualTo("John");
   }

   @Test
   void whenFindUserByStatusNativeThenFound() {
      User user = userRepository.findUserByStatusNative(0);
      assertThat(user.getStatus()).isZero();
   }

   @Test
   void whenFindUserByStatusAndNameNamedParamsThenFound() {
      User user = userRepository.findUserByStatusAndNameNamedParams(1, "John");
      assertThat(user.getStatus()).isOne();
      assertThat(user.getName()).isEqualTo("John");
   }

   @Test
   void whenFindUserByUserStatusAndUserNameThenFound() {
      User user = userRepository.findUserByUserStatusAndUserName(1, "John");
      assertThat(user.getStatus()).isOne();
      assertThat(user.getName()).isEqualTo("John");
   }

   @Test
   void whenFindUserByStatusAndNameNamedParamsNativeThenFound() {
      User user = userRepository.findUserByStatusAndNameNamedParamsNative(1, "Bob");
      assertThat(user.getStatus()).isOne();
      assertThat(user.getName()).isEqualTo("Bob");
   }

   @Test
   void whenFindUserByNameListThenAllFound() {
      List<User> users = userRepository.findUserByNameList(Arrays.asList("Bob", "Cindy"));
      assertThat(users)
            .extracting("name")
            .containsOnly("Bob", "Cindy");
   }

   @Test
   void whenUpdateUserSetStatusForNameThenUpdated() {
      int updated = userRepository.updateUserSetStatusForName(0, "John");
      assertThat(updated).isOne();

      User john = userRepository.findUserByStatusAndName(0, "John");
      assertThat(john).isNotNull();
   }

   @Test
   void whenUpdateUserSetStatusForNameNativeThenUpdated() {
      int updated = userRepository.updateUserSetStatusForNameNative(0, "John");
      assertThat(updated).isOne();

      User john = userRepository.findUserByStatusAndName(0, "John");
      assertThat(john).isNotNull();
   }

   @Test
   void whenInsertUserThenInserted() {
      User beforeInsert = userRepository.findUserByStatusAndName(0, "Mandy");
      assertThat(beforeInsert).isNull();

      userRepository.insertUser("Mandy", 20, "mandy@email.com", 0, true);

      User afterInsert = userRepository.findUserByStatusAndName(0, "Mandy");
      assertThat(afterInsert).isNotNull();
   }
}