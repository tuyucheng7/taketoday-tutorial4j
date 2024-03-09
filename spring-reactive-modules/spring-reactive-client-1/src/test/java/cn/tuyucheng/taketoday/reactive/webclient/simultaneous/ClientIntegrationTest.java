package cn.tuyucheng.taketoday.reactive.webclient.simultaneous;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
public class ClientIntegrationTest {

   private WireMockServer wireMockServer;

   private Client client;

   @Before
   public void setup() {
      wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
      wireMockServer.start();
      configureFor("localhost", wireMockServer.port());
      client = new Client(STR."http://localhost:\{wireMockServer.port()}");
   }

   @After
   public void tearDown() {
      wireMockServer.stop();
   }

   @Test
   public void givenClient_whenFetchingUsers_thenExecutionTimeIsLessThanDouble() {
      // Arrange
      int requestsNumber = 5;
      int singleRequestTime = 1000;

      for (int i = 1; i <= requestsNumber; i++) {
         stubFor(get(urlEqualTo(STR."/user/\{i}")).willReturn(aResponse().withFixedDelay(singleRequestTime)
               .withStatus(200)
               .withHeader("Content-Type", "application/json")
               .withBody(String.format("{ \"id\": %d }", i))));
      }

      List<Integer> userIds = IntStream.rangeClosed(1, requestsNumber)
            .boxed()
            .collect(Collectors.toList());

      // Act
      long start = System.currentTimeMillis();
      List<User> users = client.fetchUsers(userIds)
            .collectList()
            .block();
      long end = System.currentTimeMillis();

      // Assert
      long totalExecutionTime = end - start;

      assertEquals("Unexpected number of users", requestsNumber, users.size());
      assertTrue("Execution time is too big", 2 * singleRequestTime > totalExecutionTime);
   }
}