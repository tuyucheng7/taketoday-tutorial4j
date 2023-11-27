package cn.tuyucheng.taketoday.jobrunr;

import org.jobrunr.jobs.states.StateName;
import org.jobrunr.storage.StorageProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = DEFINED_PORT, classes = JobRunrSpringBootApplication.class)
public class JobRunrLiveTest {

   @Autowired
   TestRestTemplate restTemplate;

   @Autowired
   StorageProvider storageProvider;

   @Test
   public void givenEndpoint_whenJobEnqueued_thenJobIsProcessedWithin30Seconds() {
      String response = enqueueJobViaRest("some-input");
      assertEquals("job enqueued successfully", response);

      await().atMost(30, TimeUnit.SECONDS).until(() -> storageProvider.countJobs(StateName.SUCCEEDED) == 1);
   }

   private String enqueueJobViaRest(String input) {
      try {
         return restTemplate.getForObject(new URI(STR."http://localhost:8080/jobrunr/enqueue/\{input}"), String.class);
      } catch (Exception ignored) {
         ignored.printStackTrace();
      }
      return null;
   }
}