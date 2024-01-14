package cn.tuyucheng.taketoday.interfaceVsAbstractClass;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class InterfaceVsAbstractClassUnitTest {
   @Test
   public void givenAbstractClass_whenValidCircleUsed_thenPass() {
      CircleClass redCircle = new ChildCircleClass();
      redCircle.setColor("RED");
      assertTrue(redCircle.isValid());
   }

   @Test
   public void givenInterface_whenValidCircleWithoutStateUsed_thenPass() {
      ChidlCircleInterfaceImpl redCircleWithoutState = new ChidlCircleInterfaceImpl();
      redCircleWithoutState.setColor("RED");
      assertTrue(redCircleWithoutState.isValid());
   }
}