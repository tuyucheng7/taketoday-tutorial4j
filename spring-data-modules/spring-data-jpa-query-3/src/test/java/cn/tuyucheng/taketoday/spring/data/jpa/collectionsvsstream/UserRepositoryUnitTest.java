package cn.tuyucheng.taketoday.spring.data.jpa.collectionsvsstream;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryUnitTest {

   @Autowired
   private UserRepository userRepository;

   @BeforeEach
   void setup() {
      Faker faker = new Faker();
      List<User> people = IntStream.range(1, 100)
            .parallel()
            .mapToObj(i -> new User(faker.name()
                  .firstName(), faker.name()
                  .lastName(), faker.number()
                  .numberBetween(1, 100), i))
            .collect(Collectors.toList());
      userRepository.saveAll(people);
   }

   @AfterEach
   void tearDown() {
      userRepository.deleteAll();
   }

   @Test
   void whenAgeIs20_thenItShouldReturnAllUsersWhoseAgeIsGreaterThan20InAList() {
      List<User> users = userRepository.findByAgeGreaterThan(20);
      assertThat(users).isNotEmpty();
      assertThat(users.stream()
            .map(User::getAge)
            .allMatch(age -> age > 20)).isTrue();
   }

   @Test
   void whenAgeIs20_thenItShouldReturnAllUsersWhoseAgeIsGreaterThan20InAStream() {
      Stream<User> users = userRepository.findAllByAgeGreaterThan(20);
      assertThat(users).isNotNull();
      assertThat(users.map(User::getAge)
            .allMatch(age -> age > 20)).isTrue();
   }
}
