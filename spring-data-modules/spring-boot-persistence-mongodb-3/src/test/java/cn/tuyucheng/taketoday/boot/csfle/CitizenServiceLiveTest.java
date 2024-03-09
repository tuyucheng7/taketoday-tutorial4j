package cn.tuyucheng.taketoday.boot.csfle;

import cn.tuyucheng.taketoday.boot.csfle.data.Citizen;
import cn.tuyucheng.taketoday.boot.csfle.data.EncryptedCitizen;
import cn.tuyucheng.taketoday.boot.csfle.service.CitizenService;
import org.bson.types.Binary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DirtiesContext
@ExtendWith(SpringExtension.class)
@TestPropertySource("/embedded.properties")
@SpringBootTest(classes = MongoDbCsfleApplication.class)
class CitizenServiceLiveTest {

   @Autowired
   private MongoTemplate mongo;

   @Autowired
   private CitizenService service;

   @Test
   void givenCitizen_whenEncryptingEmail_thenEncryptedCitizenEmailMatches() {
      final Citizen citizen = new Citizen();
      citizen.setName("Foo");
      citizen.setEmail("foo@citizen.com");

      Object saved = service.save(citizen);
      if (saved instanceof EncryptedCitizen) {
         Binary encryptedEmail = service.encrypt(citizen.getEmail(), CitizenService.DETERMINISTIC_ALGORITHM);

         assertEquals(encryptedEmail, ((EncryptedCitizen) saved).getEmail());
      } else {
         assertEquals(citizen.getEmail(), ((Citizen) saved).getEmail());
      }
   }

   @Test
   void givenRandomEncryptedField_whenFilteringByField_thenDocumentNotFound() {
      Citizen john = new Citizen();
      john.setName("Jane Doe");
      john.setEmail("jane.doe@citizen.com");
      john.setBirthYear(1852);

      service.save(john);

      Query byBirthYear = new Query(Criteria.where("birthYear")
            .is(service.encrypt(john.getBirthYear(), CitizenService.RANDOM_ALGORITHM)));
      Citizen result = mongo.findOne(byBirthYear, Citizen.class);

      assertNull(result);
   }

   @Test
   void givenDeterministicallyEncryptedField_whenFilteringByField_thenDocumentFound() {
      Citizen jane = new Citizen();
      jane.setName("Jane Doe");
      jane.setEmail("jane.doe@citizen.com");
      jane.setBirthYear(1952);

      service.save(jane);
      Citizen result = service.findByEmail(jane.getEmail());

      assertNotNull(result);
   }
}