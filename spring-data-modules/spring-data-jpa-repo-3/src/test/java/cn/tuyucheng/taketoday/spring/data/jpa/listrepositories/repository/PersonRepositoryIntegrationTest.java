package cn.tuyucheng.taketoday.spring.data.jpa.listrepositories.repository;

import cn.tuyucheng.taketoday.spring.data.jpa.listrepositories.entity.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class PersonRepositoryIntegrationTest {

   @Autowired
   private PersonRepository personRepository;

   @Test
   void givenFirstName_whenCallingFindByFirstName_ThenReturnOnePerson() {
      Person person = personRepository.findByFirstName("Azhrioun");

      assertNotNull(person);
      assertEquals("Abderrahim", person.getLastName());
   }

   @Test
   void givenLastName_whenCallingFindByLastName_ThenReturnList() {
      List<Person> person = personRepository.findByLastName("Wheeler");

      assertEquals(2, person.size());
   }

   @Test
   void givenFirstName_whenCallingFindOneByFirstName_ThenReturnOnePerson() {
      Person person = personRepository.findOneByFirstName("Azhrioun");

      assertNotNull(person);
      assertEquals("Abderrahim", person.getLastName());
   }

   @Test
   void givenLastName_whenCallingFindOneByLastName_ThenReturnList() {
      List<Person> persons = personRepository.findOneByLastName("Wheeler");

      assertEquals(2, persons.size());
   }

   @Test
   void givenFirstName_whenCallingFindByFirstName_ThenThrowException() {
      IncorrectResultSizeDataAccessException exception = assertThrows(IncorrectResultSizeDataAccessException.class, () -> personRepository.findByFirstName("Stella"));

      assertEquals("Query did not return a unique result: 2 results were returned", exception.getMessage());
   }

   @Test
   void givenFirstName_whenCallingFindOneByFirstName_ThenThrowException() {
      IncorrectResultSizeDataAccessException exception = assertThrows(IncorrectResultSizeDataAccessException.class, () -> personRepository.findOneByFirstName("Stella"));

      assertEquals("Query did not return a unique result: 2 results were returned", exception.getMessage());
   }
}