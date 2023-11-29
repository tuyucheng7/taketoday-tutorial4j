package cn.tuyucheng.taketoday.systemrules;

import org.junit.jupiter.api.Test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SystemErrPrintlnUnitTest {

   @Test
   void givenTapSystemErr_whenInvokePrintln_thenOutputIsReturnedSuccessfully() throws Exception {
      String text = tapSystemErr(() -> {
         printError("An error occurred Tuyucheng Readers!!");
      });

      assertEquals("An error occurred Tuyucheng Readers!!", text.trim());
   }

   private void printError(String output) {
      System.err.println(output);
   }
}