import cn.tuyucheng.taketoday.structuredlogging.User;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class StructuredLog4jExampleUnitTest {

   Logger logger = LoggerFactory.getLogger("logger_name_example");

   @Test
   void whenInfoLoggingData_thenFormatItCorrectly() {
      User user = new User("1", "John Doe", "123456");

      logger.atInfo().setMessage("Processed user successfully")
            .addKeyValue("user_info", user)
            .log();
   }

   @Test
   void givenStructuredLog_whenUseLog4j_thenExtractCorrectInformation() {
      User user = new User("1", "John Doe", "123456");

      try {
         throwExceptionMethod();
      } catch (RuntimeException ex) {
         logger.atError().addKeyValue("user_info", user)
               .setMessage("Error processing given user")
               .addKeyValue("exception_class", ex.getClass().getSimpleName())
               .addKeyValue("error_message", ex.getMessage())
               .log();
      }
   }

   private void throwExceptionMethod() {
      throw new RuntimeException("Error saving user data", new AssertionError());
   }
}