package cn.tuyucheng.taketoday.spring.cloud.zuul.filter;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomZuulErrorFilterLiveTest {

   @Test
   void whenSendRequestWithoutService_thenCustomError() {
      final Response response = RestAssured.get("http://localhost:8080/foos/1");
      assertEquals(503, response.getStatusCode());
   }

   @Test
   void whenSendRequestWithoutCustomErrorFilter_thenError() {
      final Response response = RestAssured.get("http://localhost:8080/foos/1");
      assertEquals(500, response.getStatusCode());
   }
}