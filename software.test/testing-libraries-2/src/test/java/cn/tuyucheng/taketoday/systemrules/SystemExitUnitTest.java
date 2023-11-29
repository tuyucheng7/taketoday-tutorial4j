package cn.tuyucheng.taketoday.systemrules;

import org.junit.jupiter.api.Test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SystemExitUnitTest {

   @Test
   void givenCatchSystemExit_whenAppCallsSystemExit_thenStatusIsReturnedSuccessfully() throws Exception {
      int statusCode = catchSystemExit(() -> {
         exit();
      });
      assertEquals(1, statusCode, "status code should be 1:");
   }

   private void exit() {
      System.exit(1);
   }
}