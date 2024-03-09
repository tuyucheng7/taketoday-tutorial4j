package cn.tuyucheng.taketoday.logback;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JSONLayoutIntegrationTest {

   private static Logger logger;
   private static Logger jsonlogger;
   private ByteArrayOutputStream consoleOutput = new ByteArrayOutputStream();
   private PrintStream ps = new PrintStream(consoleOutput);

   @BeforeEach
   void setUp() {
      // Redirect console output to our stream
      System.setOut(ps);
   }

   @Test
   void givenJsonLayout_whenLogInJSON_thenOutputIsCorrectJSON() {
      logger = LoggerFactory.getLogger("jsonLogger");
      logger.debug("Debug message");
      String currentLog = consoleOutput.toString();
      assertTrue(!currentLog.isEmpty() && isValidJSON(currentLog));
   }

   @Test
   void givenJsonEncoder_whenLogInJSON_thenOutputIsCorrectJSON() {
      jsonlogger = LoggerFactory.getLogger("jsonEncoderLogger");
      jsonlogger.debug("Debug message");
      String currentLog = consoleOutput.toString();
      assertTrue(!currentLog.isEmpty() && isValidJSON(currentLog));
   }

   static boolean isValidJSON(String jsonInString) {
      try {
         final ObjectMapper mapper = new ObjectMapper();
         mapper.readTree(jsonInString);
         return true;
      } catch (IOException e) {
         return false;
      }
   }
}