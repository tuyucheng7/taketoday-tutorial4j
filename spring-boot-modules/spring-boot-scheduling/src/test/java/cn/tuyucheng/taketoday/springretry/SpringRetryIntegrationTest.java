package cn.tuyucheng.taketoday.springretry;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class, loader = AnnotationConfigContextLoader.class)
class SpringRetryIntegrationTest {

   @SpyBean
   private MyService myService;
   @Value("${retry.maxAttempts}")
   private String maxAttempts;

   @Autowired
   private RetryTemplate retryTemplate;

   @Test
   void givenRetryService_whenCallWithException_thenRetry() {
      assertThrows(RuntimeException.class, () -> myService.retryService());
   }

   @Test
   void givenRetryServiceWithRecovery_whenCallWithException_thenRetryRecover() throws SQLException {
      myService.retryServiceWithRecovery(null);
   }

   @Test
   void givenRetryServiceWithCustomization_whenCallWithException_thenRetryRecover() throws SQLException {
      myService.retryServiceWithCustomization(null);
      verify(myService, times(Integer.parseInt(maxAttempts))).retryServiceWithCustomization(any());
   }

   @Test
   void givenRetryServiceWithExternalConfiguration_whenCallWithException_thenRetryRecover() throws SQLException {
      myService.retryServiceWithExternalConfiguration(null);
      verify(myService, times(Integer.parseInt(maxAttempts))).retryServiceWithExternalConfiguration(any());
   }

   @Test
   void givenTemplateRetryService_whenCallWithException_thenRetry() {
      assertThrows(RuntimeException.class, () -> retryTemplate.execute(_ -> {
         myService.templateRetryService();
         return null;
      }));
   }
}