package cn.tuyucheng.taketoday.reactive.cors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

import cn.tuyucheng.taketoday.reactive.cors.global.CorsGlobalConfigApplication;

@SpringBootTest(classes = CorsGlobalConfigApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CorsOnGlobalConfigLiveTest {

   private static final String BASE_URL = "http://localhost:8082";
   private static final String BASE_REGULAR_URL = "/cors-on-global-config";
   private static final String BASE_EXTRA_CORS_CONFIG_URL = "/cors-on-global-config-and-more";
   private static final String BASE_FUNCTIONALS_URL = "/global-config-on-functional";
   private static final String CORS_DEFAULT_ORIGIN = "http://allowed-origin.com";

   private static WebTestClient client;

   @BeforeAll
   public static void setup() {
      client = WebTestClient.bindToServer()
            .baseUrl(BASE_URL)
            .defaultHeader("Origin", CORS_DEFAULT_ORIGIN)
            .build();
   }

   @Test
   public void whenRequestingRegularEndpoint_thenObtainResponseWithCorsHeaders() {
      ResponseSpec response = client.put()
            .uri(STR."\{BASE_REGULAR_URL}/regular-put-endpoint")
            .exchange();

      response.expectHeader()
            .valueEquals("Access-Control-Allow-Origin", CORS_DEFAULT_ORIGIN);
   }

   @Test
   public void whenRequestingRegularDeleteEndpoint_thenObtainForbiddenResponse() {
      ResponseSpec response = client.delete()
            .uri(STR."\{BASE_REGULAR_URL}/regular-delete-endpoint")
            .exchange();

      response.expectStatus()
            .isForbidden();
   }

   @Test
   public void whenPreflightAllowedDeleteEndpoint_thenObtainResponseWithCorsHeaders() {
      ResponseSpec response = client.options()
            .uri(STR."\{BASE_EXTRA_CORS_CONFIG_URL}/further-mixed-config-endpoint")
            .header("Access-Control-Request-Method", "DELETE")
            .header("Access-Control-Request-Headers", "Tuyucheng-Not-Allowed, Tuyucheng-Allowed, Tuyucheng-Other-Allowed")
            .exchange();

      response.expectHeader()
            .valueEquals("Access-Control-Allow-Origin", CORS_DEFAULT_ORIGIN);
      response.expectHeader()
            .valueEquals("Access-Control-Allow-Methods", "PUT,DELETE");
      response.expectHeader()
            .valueEquals("Access-Control-Allow-Headers", "Tuyucheng-Allowed, Tuyucheng-Other-Allowed");
      response.expectHeader()
            .exists("Access-Control-Max-Age");
   }

   @Test
   public void whenRequestAllowedDeleteEndpoint_thenObtainResponseWithCorsHeaders() {
      ResponseSpec response = client.delete()
            .uri(STR."\{BASE_EXTRA_CORS_CONFIG_URL}/further-mixed-config-endpoint")
            .exchange();

      response.expectStatus()
            .isOk();
   }

   @Test
   public void whenPreflightFunctionalEndpoint_thenObtain404Response() {
      ResponseSpec response = client.options()
            .uri(STR."\{BASE_FUNCTIONALS_URL}/cors-disabled-functional-endpoint")
            .header("Access-Control-Request-Method", "PUT")
            .exchange();

      response.expectStatus()
            .isNotFound();
   }

   @Test
   public void whenRequestFunctionalEndpoint_thenObtainResponseWithCorsHeaders() {
      ResponseSpec response = client.put()
            .uri(STR."\{BASE_FUNCTIONALS_URL}/cors-disabled-functional-endpoint")
            .exchange();

      response.expectHeader()
            .valueEquals("Access-Control-Allow-Origin", CORS_DEFAULT_ORIGIN);
   }
}