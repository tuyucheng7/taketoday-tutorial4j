package cn.tuyucheng.taketoday.system;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnvironmentVariablesUnitTest {

   @Test
   public void givenEnvVars_whenReadPath_thenGetValueinResult() {
      EnvironmentVariables environmentVariables = new EnvironmentVariables();

      Assertions.assertNotNull(environmentVariables.getPath());
   }
}
