package cn.tuyucheng.taketoday.web;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SwaggerLiveTest {
   private static final String URL_PREFIX = "http://localhost:8080/spring-security-rest/api";

   @Test
   public void whenVerifySpringFoxIsWorking_thenOK() {
      final Response response = RestAssured.get(URL_PREFIX + "/v2/api-docs");
      assertEquals(200, response.statusCode());
      System.out.println(response.asString());
   }
}