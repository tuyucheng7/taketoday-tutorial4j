package cn.tuyucheng.taketoday.spring.cloudfunction.aws;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudFunctionApplicationUnitTest {

   @LocalServerPort
   private int port;

   @Autowired
   private TestRestTemplate testRestTemplate;

   @Test
   void givenAString_whenReverseStringCloudFunctionInvoked_thenStringIsReversed() {
      assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/reverseString/HelloWorld", String.class)).isEqualTo("dlroWolleH");
   }

   @Test
   void givenAString_whenGreeterCloudFunctionInvoked_thenPrintsGreeting() {
      assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/greeter/BaeldungUser", String.class)).isEqualTo("Hello BaeldungUser, and welcome to Spring Cloud Function!!!");
   }
}