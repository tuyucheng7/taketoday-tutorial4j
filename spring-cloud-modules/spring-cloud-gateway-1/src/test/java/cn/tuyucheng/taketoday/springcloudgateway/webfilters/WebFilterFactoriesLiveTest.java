package cn.tuyucheng.taketoday.springcloudgateway.webfilters;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("webfilters")
class WebFilterFactoriesLiveTest {

   @LocalServerPort
   String port;

   @Autowired
   private WebTestClient client;

   @Autowired
   private TestRestTemplate restTemplate;

   @BeforeEach
   void configureClient() {
      client = WebTestClient.bindToServer()
            .baseUrl("http://localhost:" + port)
            .build();
   }

   @Test
   void whenCallGetThroughGateway_thenAllHTTPRequestHeadersParametersAreSet() throws JSONException {
      String url = "http://localhost:" + port + "/get";
      ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

      JSONObject json = new JSONObject(response.getBody());
      JSONObject headers = json.getJSONObject("headers");
      assertThat(headers.getString("My-Header-Good")).isEqualTo("Good");
      assertThat(headers.getString("My-Header-Bad")).isEqualTo("Good");
      assertThat(headers.getString("My-Header-Set")).isEqualTo("Set");
      assertTrue(headers.isNull("My-Header-Remove"));
      JSONObject vars = json.getJSONObject("args");
      assertThat(vars.getString("var")).isEqualTo("good");
   }

   @Test
   void whenCallHeaderPostThroughGateway_thenAllHTTPResponseHeadersAreSet() {
      ResponseSpec response = client.post()
            .uri("/header/post")
            .exchange();

      response.expectStatus()
            .isOk()
            .expectHeader()
            .valueEquals("My-Header-Rewrite", "password=***")
            .expectHeader()
            .valueEquals("My-Header-Set", "Set")
            .expectHeader()
            .valueEquals("My-Header-Good", "Good")
            .expectHeader()
            .doesNotExist("My-Header-Remove");
   }

   @Test
   void whenCallPostThroughGateway_thenBodyIsRetrieved() throws JSONException {
      String url = "http://localhost:" + port + "/post";

      HttpEntity<String> entity = new HttpEntity<>("content", new HttpHeaders());

      ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

      JSONObject json = new JSONObject(response.getBody());
      JSONObject data = json.getJSONObject("json");
      assertThat(data.getString("message")).isEqualTo("CONTENT");
   }

   @Test
   void whenCallPutThroughGateway_thenBodyIsRetrieved() throws JSONException {
      String url = "http://localhost:" + port + "/put";

      HttpEntity<String> entity = new HttpEntity<>("CONTENT", new HttpHeaders());

      ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

      JSONObject json = new JSONObject(response.getBody());
      assertThat(json.getString("message")).isEqualTo("New Body");
   }

   @Test
   void whenCallDeleteThroughGateway_thenIsUnauthorizedCodeIsSet() {
      ResponseSpec response = client.delete()
            .uri("/delete")
            .exchange();

      response.expectStatus()
            .isUnauthorized();
   }

   @Test
   void whenCallFakePostThroughGateway_thenIsUnauthorizedCodeIsSet() {
      ResponseSpec response = client.post()
            .uri("/fake/post")
            .exchange();

      response.expectStatus()
            .is3xxRedirection();
   }

   @Test
   void whenCallStatus504ThroughGateway_thenCircuitBreakerIsExecuted() throws JSONException {
      String url = "http://localhost:" + port + "/status/504";
      ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

      JSONObject json = new JSONObject(response.getBody());
      assertThat(json.getString("url")).contains("anything");
   }
}