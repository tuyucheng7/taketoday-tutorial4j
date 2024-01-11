package cn.tuyucheng.taketoday.jpa;

import cn.tuyucheng.taketoday.jpa.domain.Passenger;
import cn.tuyucheng.taketoday.jpa.repository.PassengerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.IsNot.not;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {JpaApplication.class})
@DirtiesContext
class PassengerRepositoryIntegrationTest {

   @PersistenceContext
   private EntityManager entityManager;
   @Autowired
   private PassengerRepository repository;

   @BeforeEach
   @Transactional
   void before() {
      entityManager.persist(Passenger.from("Jill", "Smith"));
      entityManager.persist(Passenger.from("Eve", "Jackson"));
      entityManager.persist(Passenger.from("Fred", "Bloggs"));
      entityManager.persist(Passenger.from("Ricki", "Bobbie"));
      entityManager.persist(Passenger.from("Siya", "Kolisi"));
   }

   @Transactional
   @Test
   void givenPassengers_whenMatchingIgnoreCase_thenExpectedReturned() {
      Passenger jill = Passenger.from("Jill", "Smith");
      Passenger eve = Passenger.from("Eve", "Jackson");
      Passenger fred = Passenger.from("Fred", "Bloggs");
      Passenger siya = Passenger.from("Siya", "Kolisi");
      Passenger ricki = Passenger.from("Ricki", "Bobbie");

      List<Passenger> passengers = repository.findByFirstNameIgnoreCase("FRED");

      assertThat(passengers, contains(fred));
      assertThat(passengers, not(contains(eve)));
      assertThat(passengers, not(contains(siya)));
      assertThat(passengers, not(contains(jill)));
      assertThat(passengers, not(contains(ricki)));
   }
}