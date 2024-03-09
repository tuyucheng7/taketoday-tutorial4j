package cn.tuyucheng.taketoday.reflection.privatemethods;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static cn.tuyucheng.taketoday.reflection.privatemethods.Utils.validateAndDouble;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UtilsUnitTest {

   // Let's start with the tests of the public API
   @Test
   void givenNull_WhenValidateAndDouble_ThenThrows() {
      assertThrows(IllegalArgumentException.class, () -> validateAndDouble(null));
   }

   @Test
   void givenANonNullInteger_WhenValidateAndDouble_ThenDoublesIt() {
      assertEquals(4, validateAndDouble(2));
   }

   // Further on, let's test the private method
   @Test
   void givenNull_WhenDoubleInteger_ThenNull() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
      assertEquals(null, getDoubleIntegerMethod().invoke(null, new Integer[]{null}));
   }

   @Test
   void givenANonNullInteger_WhenDoubleInteger_ThenDoubleIt() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
      assertEquals(74, getDoubleIntegerMethod().invoke(null, 37));
   }

   private Method getDoubleIntegerMethod() throws NoSuchMethodException {
      Method method = Utils.class.getDeclaredMethod("doubleInteger", Integer.class);
      method.setAccessible(true);
      return method;
   }

}
