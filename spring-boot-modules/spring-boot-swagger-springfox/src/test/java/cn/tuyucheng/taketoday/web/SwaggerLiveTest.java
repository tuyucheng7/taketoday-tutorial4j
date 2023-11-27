package cn.tuyucheng.taketoday.web;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SwaggerLiveTest {
   private static final String URL_PREFIX = "http://localhost:8080/spring-security-rest/api";

   @Test
   void whenVerifySpringFoxIsWorking_thenOK() {
      final Response response = RestAssured.get(STR."\{URL_PREFIX}/v2/api-docs");
      assertEquals(200, response.statusCode());
      System.out.println(response.asString());
   }
}