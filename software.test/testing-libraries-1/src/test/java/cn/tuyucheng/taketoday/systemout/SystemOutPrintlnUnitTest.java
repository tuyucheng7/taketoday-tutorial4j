package cn.tuyucheng.taketoday.systemout;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;

class SystemOutPrintlnUnitTest {

   private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
   private final PrintStream standardOut = System.out;

   @BeforeEach
   public void setUp() {
      System.setOut(new PrintStream(outputStreamCaptor));
   }

   @AfterEach
   public void tearDown() {
      System.setOut(standardOut);
   }

   @Test
   void givenSystemOutRedirection_whenInvokePrintln_thenOutputCaptorSuccess() {
      print("Hello Tuyucheng Readers!!");

      Assert.assertEquals("Hello Tuyucheng Readers!!", outputStreamCaptor.toString()
            .trim());
   }

   @Test
   void givenTapSystemOut_whenInvokePrintln_thenOutputIsReturnedSuccessfully() throws Exception {

      String text = tapSystemOut(() -> {
         print("Hello Tuyucheng Readers!!");
      });

      Assert.assertEquals("Hello Tuyucheng Readers!!", text.trim());
   }

   private void print(String output) {
      System.out.println(output);
   }

}
