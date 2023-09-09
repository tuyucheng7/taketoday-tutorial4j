package cn.tuyucheng.taketoday.abstractclass.indepedentmethod;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractIndependentUnitTest {

   @Test
   void givenNonAbstractMethod_whenConcreteImpl_testCorrectBehaviour() {
      ConcreteImpl conClass = new ConcreteImpl();
      String actual = conClass.defaultImpl();

      assertEquals("DEFAULT-1", actual);
   }

   @Test
   void givenNonAbstractMethod_whenMockitoMock_testCorrectBehaviour() {
      AbstractIndependent absCls = Mockito.mock(AbstractIndependent.class, Mockito.CALLS_REAL_METHODS);
      assertEquals("DEFAULT-1", absCls.defaultImpl());
   }
}