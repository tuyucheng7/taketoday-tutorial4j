package cn.tuyucheng.taketoday.p6spy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class JDBCControllerUnitTest {

   @Value("http://localhost:${local.server.port}/jdbc")
   private String localhost;

   private final RestTemplate restTemplate = new RestTemplate();

   @Test
   void select() {
      restTemplate.getForEntity(localhost + "/commit", String.class);
   }

   @Test
   void rollback() {
      restTemplate.getForEntity(localhost + "/rollback", String.class);
   }

   @Test
   void error() {
      restTemplate.getForEntity(localhost + "/query-error", String.class);
   }
}