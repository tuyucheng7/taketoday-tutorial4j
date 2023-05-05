package cn.tuyucheng.taketoday.springcassandra;

import cn.tuyucheng.taketoday.springcassandra.model.Person;
import cn.tuyucheng.taketoday.springcassandra.repository.PersonRepository;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = CassandraDatesLiveTest.Initializer.class)
@EnableConfigurationProperties
class CassandraDatesLiveTest {

   private static final String KEYSPACE_NAME = "test";

   @Container
   protected static final CassandraContainer cassandra = (CassandraContainer) new CassandraContainer("cassandra:3.11.2")
         .withExposedPorts(9042);

   @Autowired
   private PersonRepository personRepository;

   public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
      @Override
      public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
         TestPropertyValues.of(
               "spring.data.cassandra.keyspace-name=" + KEYSPACE_NAME,
               "spring.data.cassandra.contact-points=" + cassandra.getContainerIpAddress(),
               "spring.data.cassandra.port=" + cassandra.getMappedPort(9042)
         ).applyTo(configurableApplicationContext.getEnvironment());

         createKeyspace(cassandra.getCluster());
      }
   }

   private static void createKeyspace(Cluster cluster) {
      try (Session session = cluster.connect()) {
         session.execute("CREATE KEYSPACE IF NOT EXISTS " + KEYSPACE_NAME + " WITH replication = \n" +
                         "{'class':'SimpleStrategy','replication_factor':'1'};");
      }
   }

   @Nested
   class SpringCassandraLiveTest {

      @Test
      void givenCassandraContainer_whenSpringContextIsBootstrapped_thenContainerIsRunningWithNoExceptions() {
         assertThat(cassandra.isRunning()).isTrue();
      }
   }

   @Nested
   class PersonRepositoryLiveTest {

      @Test
      void givenValidPersonRecord_whenSavingIt_thenDataIsPersisted() {
         UUID personId = UUIDs.timeBased();
         Person newPerson = new Person(personId, "Luka", "Modric");
         personRepository.save(newPerson);

         List<Person> savedPersons = personRepository.findAllById(List.of(personId));
         assertThat(savedPersons.get(0)).isEqualTo(newPerson);
      }

      @Test
      void givenValidPersonUsingLocalDate_whenSavingIt_thenDataIsPersisted() {
         UUID personId = UUIDs.timeBased();
         Person newPerson = new Person(personId, "Luka", "Modric");
         newPerson.setBirthDate(LocalDate.of(1985, 9, 9));
         personRepository.save(newPerson);

         List<Person> savedPersons = personRepository.findAllById(List.of(personId));
         assertThat(savedPersons.get(0)).isEqualTo(newPerson);
      }

      @Test
      void givenValidPersonUsingLocalDateTime_whenSavingIt_thenDataIsPersisted() {
         UUID personId = UUIDs.timeBased();
         Person newPerson = new Person(personId, "Luka", "Modric");
         newPerson.setLastVisitedDate(LocalDateTime.of(2021, 7, 13, 11, 30));
         personRepository.save(newPerson);

         List<Person> savedPersons = personRepository.findAllById(List.of(personId));
         assertThat(savedPersons.get(0)).isEqualTo(newPerson);
      }

      @Test
      void givenValidPersonUsingLegacyDate_whenSavingIt_thenDataIsPersisted() {
         UUID personId = UUIDs.timeBased();
         Person newPerson = new Person(personId, "Luka", "Modric");
         newPerson.setLastPurchasedDate(new Date(LocalDate.of(2021, 7, 13).toEpochDay()));
         personRepository.save(newPerson);

         List<Person> savedPersons = personRepository.findAllById(List.of(personId));
         assertThat(savedPersons.get(0)).isEqualTo(newPerson);
      }
   }
}