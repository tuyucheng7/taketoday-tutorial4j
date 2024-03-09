package cn.tuyucheng.taketoday.exceptions.nosuchfielderror;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FieldErrorExampleUnitTest {

   @Test
   public void whenDependentMessage_returnMessage() {

      String dependentMessage = FieldErrorExample.getDependentMessage();
      assertTrue("Hello Tuyucheng!!".equals(dependentMessage));
   }

}