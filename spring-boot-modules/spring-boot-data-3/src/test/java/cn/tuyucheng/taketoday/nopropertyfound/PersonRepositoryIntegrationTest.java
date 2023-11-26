package cn.tuyucheng.taketoday.nopropertyfound;

import cn.tuyucheng.taketoday.nopropertyfound.model.Person;
import cn.tuyucheng.taketoday.nopropertyfound.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class PersonRepositoryIntegrationTest {

   @Autowired
   private PersonRepository personRepository;

   @Test
   void givenQueryMethod_whenUsingValidProperty_thenCorrect() {
      Person person = personRepository.findByFirstName("Azhrioun");

      assertNotNull(person);
      assertEquals("Abderrahim", person.getLastName());
   }
}