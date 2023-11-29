package cn.tuyucheng.taketoday.migration.junit4;

import cn.tuyucheng.taketoday.migration.junit4.rules.TraceUnitTestRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleExampleUnitTest {

   private static final Logger LOGGER = LoggerFactory.getLogger(RuleExampleUnitTest.class);

   @Rule
   public final TraceUnitTestRule traceRuleTests = new TraceUnitTestRule();

   @Test
   public void whenTracingTests() {
      LOGGER.debug("This is my test");
      /*...*/
   }
}