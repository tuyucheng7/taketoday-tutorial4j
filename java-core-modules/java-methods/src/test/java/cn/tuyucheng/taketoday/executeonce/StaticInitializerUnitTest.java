package cn.tuyucheng.taketoday.executeonce;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class StaticInitializerUnitTest {

   @Test
   void whenLoadingStaticInitializer_thenCallCountIsOne() {
      Assertions.assertEquals(1, StaticInitializer.CALL_COUNT);
   }

   @Test
   void whenInitializingStaticInitializer_thenCallCountStaysOne() {
      StaticInitializer staticInitializer = new StaticInitializer();
      assertEquals(1, StaticInitializer.CALL_COUNT);
   }
}