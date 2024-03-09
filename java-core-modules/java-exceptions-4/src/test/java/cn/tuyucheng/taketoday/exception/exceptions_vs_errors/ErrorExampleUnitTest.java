package cn.tuyucheng.taketoday.exception.exceptions_vs_errors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ErrorExampleUnitTest {

   @Test
   public void whenMainMethodIsRun_thenStackOverflowError() {
      Assertions.assertThrows(AssertionError.class,
            () -> ErrorExample.main(null));
   }
}
