package cn.tuyucheng.taketoday.commandline;

import org.junit.jupiter.api.Test;

public class CommandLineWithoutErrorHandlingUnitTest {

   @Test(expected = NullPointerException.class)
   public void givenNullCommandLineArgument_whenPassedToMainFunction_thenExpectNullPointerException() {
      CommandLineWithoutErrorHandling.main(null);
   }
}