package cn.tuyucheng.taketoday.testloglevel;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.system.OutputCaptureRule;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = TestLogLevelApplication.class)
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, OAuth2ResourceServerAutoConfiguration.class})
@ActiveProfiles("logback-test2")
public class LogbackMultiProfileTestLogLevelIntegrationTest {

   @Autowired
   private TestRestTemplate restTemplate;

   @Rule
   public OutputCaptureRule outputCapture = new OutputCaptureRule();

   private String baseUrl = "/testLogLevel";

   @Test
   public void givenErrorRootLevelAndTraceLevelForOurPackage_whenCall_thenPrintTraceLogsForOurPackage() {
      ResponseEntity<String> response = restTemplate.getForEntity(baseUrl, String.class);

      assertThat(response.getStatusCode().value()).isEqualTo(200);
      assertThatOutputContainsLogForOurPackage("TRACE");
   }

   @Test
   public void givenErrorRootLevelAndTraceLevelForOurPackage_whenCall_thenNoTraceLogsForOtherPackages() {
      ResponseEntity<String> response = restTemplate.getForEntity(baseUrl, String.class);

      assertThat(response.getStatusCode().value()).isEqualTo(200);
      assertThatOutputDoesntContainLogForOtherPackages("TRACE");
   }

   @Test
   public void givenErrorRootLevelAndTraceLevelForOurPackage_whenCall_thenPrintErrorLogs() {
      ResponseEntity<String> response = restTemplate.getForEntity(baseUrl, String.class);

      assertThat(response.getStatusCode().value()).isEqualTo(200);
      assertThatOutputContainsLogForOurPackage("ERROR");
      assertThatOutputContainsLogForOtherPackages("ERROR");
   }

   private void assertThatOutputContainsLogForOurPackage(String level) {
      assertThat(outputCapture.toString()).containsPattern(STR."TestLogLevelController.*\{level}.*");
   }

   private void assertThatOutputDoesntContainLogForOtherPackages(String level) {
      assertThat(outputCapture.toString().replaceAll("(?m)^.*TestLogLevelController.*$", "")).doesNotContain(level);
   }

   private void assertThatOutputContainsLogForOtherPackages(String level) {
      assertThat(outputCapture.toString().replaceAll("(?m)^.*TestLogLevelController.*$", "")).contains(level);
   }
}