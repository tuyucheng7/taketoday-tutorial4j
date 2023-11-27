package cn.tuyucheng.taketoday.web.controller;

import cn.tuyucheng.taketoday.web.log.app.Application;
import cn.tuyucheng.taketoday.web.log.data.TaxiRide;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaxiFareControllerIntegrationTest {

   @LocalServerPort
   private int port;

   @Test
   void givenRequest_whenFetchTaxiFareRateCard_thanOK() {
      String URL = STR."http://localhost:\{port}/spring-rest";
      TestRestTemplate testRestTemplate = new TestRestTemplate();
      TaxiRide taxiRide = new TaxiRide(true, 10l);
      String fare = testRestTemplate.postForObject(
            STR."\{URL}/taxifare/calculate/",
            taxiRide, String.class);

      assertThat(fare, equalTo("200"));
   }
}