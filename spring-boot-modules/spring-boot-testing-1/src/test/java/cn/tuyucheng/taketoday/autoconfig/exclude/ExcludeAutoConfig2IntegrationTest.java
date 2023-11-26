package cn.tuyucheng.taketoday.autoconfig.exclude;

import cn.tuyucheng.taketoday.boot.Application;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ExcludeAutoConfig2IntegrationTest {

   /**
    * Encapsulates the random port the test server is listening on.
    */
   @LocalServerPort
   private int port;

   @Test
   void givenSecurityConfigExcluded_whenAccessHome_thenNoAuthenticationRequired() {
      int statusCode = RestAssured.get("http://localhost:" + port).statusCode();
      assertEquals(HttpStatus.OK.value(), statusCode);
   }
}