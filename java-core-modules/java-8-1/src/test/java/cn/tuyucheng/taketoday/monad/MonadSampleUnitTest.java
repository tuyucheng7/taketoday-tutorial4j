package cn.tuyucheng.taketoday.monad;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MonadSampleUnitTest {

   @Test
   public void whenNotUsingMonad_shouldBeOk() {
      MonadSample1 test = new MonadSample1();
      assertEquals(6.0, test.apply(2), 0.000);
   }

   @Test
   public void whenNotUsingMonadButUsingTempVars_shouldBeOk() {
      MonadSample2 test = new MonadSample2();
      assertEquals(6.0, test.apply(2), 0.000);
   }

   @Test
   public void whenUsingMonad_shouldBeOk() {
      MonadSample3 test = new MonadSample3();
      assertEquals(6.0, test.apply(2), 0.000);
   }

   @Test
   public void whenTestingMonadProperties_shouldBeOk() {
      MonadSample4 test = new MonadSample4();
      assertEquals(true, test.leftIdentity());
      assertEquals(true, test.rightIdentity());
      assertEquals(true, test.associativity());
   }

   @Test
   public void whenBreakingMonadProperties_shouldBeFalse() {
      MonadSample5 test = new MonadSample5();
      assertEquals(false, test.fail());
   }
}
