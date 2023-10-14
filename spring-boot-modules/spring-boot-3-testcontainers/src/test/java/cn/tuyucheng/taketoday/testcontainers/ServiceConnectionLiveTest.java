package cn.tuyucheng.taketoday.testcontainers;

import cn.tuyucheng.taketoday.testcontainers.support.MiddleEarthCharacter;
import cn.tuyucheng.taketoday.testcontainers.support.MiddleEarthCharactersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@Testcontainers
@SpringBootTest(webEnvironment = DEFINED_PORT)
@DirtiesContext(classMode = AFTER_CLASS)
// Testcontainers require a valid docker installation.
// When running the tests, ensure you have a valid Docker environment
class ServiceConnectionLiveTest {

   @Container
   @ServiceConnection
   static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

   @Autowired
   private MiddleEarthCharactersRepository repository;

   @BeforeEach
   void beforeEach() {
      repository.deleteAll();
   }

   @Test
   void whenRequestingHobbits_thenReturnFrodoAndSam() {
      repository.saveAll(List.of(
            new MiddleEarthCharacter("Frodo", "hobbit"),
            new MiddleEarthCharacter("Samwise", "hobbit"),
            new MiddleEarthCharacter("Aragon", "human"),
            new MiddleEarthCharacter("Gandalf", "wizard")
      ));

      when().get("/characters?race=hobbit")
            .then().statusCode(200)
            .and().body("name", hasItems("Frodo", "Samwise"));
   }
}