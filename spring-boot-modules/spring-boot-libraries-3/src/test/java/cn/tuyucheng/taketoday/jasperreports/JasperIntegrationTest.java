package cn.tuyucheng.taketoday.jasperreports;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JasperIntegrationTest {

   protected Logger logger = LoggerFactory.getLogger(JasperIntegrationTest.class.getName());

   @Autowired
   TestRestTemplate template;

   @Test
   void testGetReport() throws InterruptedException {
      List<HttpStatusCode> responses = new ArrayList<>();
      Random r = new Random();
      int i = 0;

      long start = System.currentTimeMillis();
      for (; i < 20; i++) {
         new Thread(() -> {
            int age = r.nextInt(99);
            long start1 = System.currentTimeMillis();
            ResponseEntity<InputStreamResource> res = template.getForEntity("/pdf/fv/{age}", InputStreamResource.class, age);
            logger.info("Response (" + (System.currentTimeMillis() - start1) + "): " + res.getStatusCode());
            responses.add(res.getStatusCode());
            try {
               Thread.sleep(50);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }).start();
      }

      while (responses.size() != i) {
         Thread.sleep(500);
         if (System.currentTimeMillis() - start > 10000) break;
      }

      logger.info("Test finished: ok->{}, expected->{}", responses.size(), i);
      assertEquals(i, responses.size());
   }
}