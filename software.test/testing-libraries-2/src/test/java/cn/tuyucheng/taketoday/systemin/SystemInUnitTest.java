package cn.tuyucheng.taketoday.systemin;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import org.junit.jupiter.api.Test;
import uk.org.webcompere.systemstubs.SystemStubs;

import java.io.ByteArrayInputStream;

import static cn.tuyucheng.taketoday.systemin.Application.NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SystemInUnitTest {
   private void provideInput(String data) {
      ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
      System.setIn(testIn);
   }

   @Test
   void givenName_whenReadNameFromInput_thenReturnCorrectResult() {
      provideInput("Tuyucheng");
      String input = Application.readName();
      assertEquals(NAME.concat("Tuyucheng"), input);
   }

   @Test
   void givenName_whenReadWithSystemLambda_thenReturnCorrectResult() throws Exception {
      SystemLambda.withTextFromSystemIn("Tuyucheng")
            .execute(() -> assertEquals(NAME.concat("Tuyucheng"), Application.readName()));
   }

   @Test
   void givenName_whenReadWithSystemStubs_thenReturnCorrectResult() throws Exception {
      SystemStubs.withTextFromSystemIn("Tuyucheng")
            .execute(() -> {
               assertThat(Application.readName())
                     .isEqualTo(NAME.concat("Tuyucheng"));
            });
   }
}