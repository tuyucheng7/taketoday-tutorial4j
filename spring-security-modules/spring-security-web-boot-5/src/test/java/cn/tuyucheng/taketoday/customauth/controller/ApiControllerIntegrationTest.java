package cn.tuyucheng.taketoday.customauth.controller;

import cn.tuyucheng.taketoday.customauth.SpringSecurityApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = SpringSecurityApplication.class,
      webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(SpringExtension.class)
class ApiControllerIntegrationTest {

   private static final String API_ENDPOINT = "http://localhost:8080/app/api/hello";
   private final TestRestTemplate restTemplate = new TestRestTemplate();

   @Test
   void givenAuthHeaderSecretIsValid_whenApiControllerCalled_thenReturnOk() throws Exception {
      HttpHeaders headers = new HttpHeaders();
      headers.add("x-auth-secret-key", "test-secret");

      ResponseEntity<String> response = restTemplate.exchange(new URI(API_ENDPOINT), HttpMethod.GET,
            new HttpEntity<>(headers), String.class);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals("hello", response.getBody());
   }

   @Test
   void givenAAuthHeaderIsInvalid_whenApiControllerCalled_thenReturnUnAuthorised() throws Exception {
      HttpHeaders headers = new HttpHeaders();
      headers.add("x-auth-secret-key", "invalid");

      ResponseEntity<String> response = restTemplate.exchange(new URI(API_ENDPOINT), HttpMethod.GET,
            new HttpEntity<>(headers), String.class);

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
   }

   @Test
   void givenAuthHeaderNameIsInValid_whenApiControllerCalled_thenReturnUnAuthorised() throws Exception {
      HttpHeaders headers = new HttpHeaders();
      headers.add("x-auth-secret", "test-secret");

      ResponseEntity<String> response = restTemplate.exchange(new URI(API_ENDPOINT), HttpMethod.GET,
            new HttpEntity<>(headers), String.class);

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
   }

   @Test
   void givenAuthHeaderIsMissing_whenApiControllerCalled_thenReturnUnAuthorised() throws Exception {
      HttpHeaders headers = new HttpHeaders();

      ResponseEntity<String> response = restTemplate.exchange(new URI(API_ENDPOINT), HttpMethod.GET,
            new HttpEntity<>(headers), String.class);

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
   }
}