package cn.tuyucheng.taketoday.spring.cloud.apigateway.fallback;

import com.netflix.hystrix.exception.HystrixTimeoutException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class WeatherServiceFallbackUnitTest {

   private static final String ROUTE = "weather-service";

   @Autowired
   private WeatherServiceFallback fallback;

   @Test
   void testWhenGetRouteThenReturnWeatherServiceRoute() {
      assertEquals(ROUTE, fallback.getRoute());
   }

   @Test
   void testFallbackResponse_whenHystrixException_thenGatewayTimeout() throws Exception {
      HystrixTimeoutException exception = new HystrixTimeoutException();
      ClientHttpResponse response = fallback.fallbackResponse(ROUTE, exception);

      assertEquals(HttpStatus.GATEWAY_TIMEOUT, response.getStatusCode());
   }

   @Test
   void testFallbackResponse_whenNonHystrixException_thenInternalServerError() throws Exception {
      RuntimeException exception = new RuntimeException("Test exception");
      ClientHttpResponse response = fallback.fallbackResponse(ROUTE, exception);

      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
   }
}