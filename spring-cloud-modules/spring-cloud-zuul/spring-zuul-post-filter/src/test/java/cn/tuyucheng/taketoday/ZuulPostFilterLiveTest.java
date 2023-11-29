package cn.tuyucheng.taketoday;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ZuulPostFilterLiveTest {

   @LocalServerPort
   private int port;
   private static final String SIMPLE_GREETING = "/api/greeting/simple";
   private TestRestTemplate restTemplate = new TestRestTemplate();

   @Test
   void whenClientCallApi_thenLogAndReturnResponseBody() {
      String url = "http://localhost:" + port + SIMPLE_GREETING;
      ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
      assertTrue(response.getStatusCode().is2xxSuccessful());
      assertEquals(response.getBody(), "Hi");
   }
}