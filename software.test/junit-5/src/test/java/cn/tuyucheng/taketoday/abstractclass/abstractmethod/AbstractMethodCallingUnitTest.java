package cn.tuyucheng.taketoday.abstractclass.abstractmethod;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractMethodCallingUnitTest {

   private AbstractMethodCalling cls;

   @BeforeEach
   void setup() {
      cls = Mockito.mock(AbstractMethodCalling.class);
   }

   @Test
   void givenDefaultImpl_whenMockAbstractFunc_thenExpectedBehaviour() {
      Mockito.when(cls.abstractFunc()).thenReturn("Abstract");
      Mockito.doCallRealMethod().when(cls).defaultImpl();

      // validate result by mock abstractFun's behaviour
      assertEquals("Abstract Default", cls.defaultImpl());

      // check the value with null response from abstract method
      Mockito.doReturn(null).when(cls).abstractFunc();
      assertEquals("Default", cls.defaultImpl());
   }
}