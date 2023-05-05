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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = CassandraLoggingLiveTest.Initializer.class)
@EnableConfigurationProperties
class CassandraLoggingLiveTest {

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

         enableRequestTracker();
         createKeyspace(cassandra.getCluster());
      }
   }

   private static void enableRequestTracker() {
      System.setProperty("datastax-java-driver.advanced.request-tracker.class", "RequestLogger");
      System.setProperty("datastax-java-driver.advanced.request-tracker.logs.success.enabled", "true");
      System.setProperty("datastax-java-driver.advanced.request-tracker.logs.slow.enabled", "true");
      System.setProperty("datastax-java-driver.advanced.request-tracker.logs.error.enabled", "true");

      System.setProperty("datastax-java-driver.advanced.request-tracker.logs.show-values", "false");
      System.setProperty("datastax-java-driver.advanced.request-tracker.logs.max-value-length", "100");
      System.setProperty("datastax-java-driver.advanced.request-tracker.logs.max-values", "100");

      System.setProperty("datastax-java-driver.advanced.request-tracker.logs.slow.threshold ", "1 second");
      System.setProperty("datastax-java-driver.advanced.request-tracker.logs.show-stack-trace", "true");
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
      void givenExistingPersonRecord_whenUpdatingIt_thenRecordIsUpdated() {
         UUID personId = UUIDs.timeBased();
         Person existingPerson = new Person(personId, "Luka", "Modric");
         personRepository.save(existingPerson);
         existingPerson.setFirstName("Marko");
         personRepository.save(existingPerson);

         List<Person> savedPersons = personRepository.findAllById(List.of(personId));
         assertThat(savedPersons.get(0).getFirstName()).isEqualTo("Marko");
      }

      @Test
      void givenExistingPersonRecord_whenDeletingIt_thenRecordIsDeleted() {
         UUID personId = UUIDs.timeBased();
         Person existingPerson = new Person(personId, "Luka", "Modric");

         personRepository.delete(existingPerson);

         List<Person> savedPersons = personRepository.findAllById(List.of(personId));
         assertThat(savedPersons.isEmpty()).isTrue();
      }
   }
}