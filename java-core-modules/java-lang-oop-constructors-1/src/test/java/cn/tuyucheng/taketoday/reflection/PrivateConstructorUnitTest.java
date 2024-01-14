package cn.tuyucheng.taketoday.reflection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

public class PrivateConstructorUnitTest {

   @Test
   public void whenConstructorIsPrivate_thenInstanceSuccess() throws Exception {
      Constructor<PrivateConstructorClass> pcc = PrivateConstructorClass.class.getDeclaredConstructor();
      pcc.setAccessible(true);
      PrivateConstructorClass privateConstructorInstance = pcc.newInstance();
      Assertions.assertTrue(privateConstructorInstance instanceof PrivateConstructorClass);
   }
}