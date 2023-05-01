package cn.tuyucheng.taketoday.problem.web;

import cn.tuyucheng.taketoday.problem.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeControllerIntegrationTest {

   public static final Logger log = LoggerFactory.getLogger(EmployeeControllerIntegrationTest.class);

   @LocalServerPort
   private int port;

   private WebClient webClient;
   private RestTemplate restTemplate;

   @BeforeEach
   void setUp() {
      String baseUrl = "http://localhost:" + this.port;
      this.webClient = WebClient.builder().baseUrl(baseUrl).build();
      this.restTemplate = new RestTemplate();
      this.restTemplate.setUriTemplateHandler(new RootUriTemplateHandler(baseUrl));
   }

   @Test
   void givenIncorrectId_whenCalledGetRequestV1_thenFails() {
      try {
         this.restTemplate.getForObject("/employees/v1/101", Employee.class);
      } catch (RestClientResponseException ex) {
         ProblemDetail pd = ex.getResponseBodyAs(ProblemDetail.class);
         System.out.println(format(ex.getStatusCode(), ex.getResponseHeaders(), pd));

         assertEquals(404, Objects.requireNonNull(pd).getStatus());
      }
   }

   @Test
   void givenIncorrectId_whenCalledGetRequestV2_thenFails() {
      try {
         this.restTemplate.getForObject("/employees/v2/101", Employee.class);
      } catch (RestClientResponseException ex) {
         ProblemDetail pd = ex.getResponseBodyAs(ProblemDetail.class);
         System.out.println(format(ex.getStatusCode(), ex.getResponseHeaders(), pd));

         assertEquals("Employee id '101' does not exist", pd.getDetail());
         assertEquals(404, pd.getStatus());
         assertEquals("Record Not Found", pd.getTitle());
         assertEquals("https://my-app-host.com/errors/not-found", pd.getType().toString());
         assertEquals("localhost", pd.getProperties().get("hostname"));
      }
   }

   @Test
   void givenIncorrectId_whenCalledGetRequestWithWebFlux_thenFails() {
      this.webClient
            .get().uri("/employees/v2/101")
            .retrieve()
            .bodyToMono(String.class)
            .doOnNext(log::info)
            .onErrorResume(WebClientResponseException.class, ex -> {
               ProblemDetail pd = ex.getResponseBodyAs(ProblemDetail.class);

               assertEquals("Employee id '101' does not exist", pd.getDetail());
               assertEquals(404, pd.getStatus());
               assertEquals("Record Not Found", pd.getTitle());
               assertEquals(URI.create("https://my-app-host.com/errors/not-found"), pd.getType());
               assertEquals("localhost", pd.getProperties().get("hostname"));
               System.out.println(format(ex.getStatusCode(), ex.getHeaders(), pd));

               return Mono.empty();
            })
            .block();
   }

   private String format(HttpStatusCode statusCode, @Nullable HttpHeaders headers, ProblemDetail body) {
      return "\nHTTP Status " + statusCode + "\n\n" +
             (headers != null ? headers : HttpHeaders.EMPTY).entrySet().stream()
                   .map(entry -> entry.getKey() + ":" + entry.getValue())
                   .collect(Collectors.joining("\n")) +
             "\n\n" + body;
   }
}