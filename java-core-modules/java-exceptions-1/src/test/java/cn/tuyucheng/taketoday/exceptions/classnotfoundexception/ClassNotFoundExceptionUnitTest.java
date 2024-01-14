package cn.tuyucheng.taketoday.exceptions.classnotfoundexception;

import org.junit.jupiter.api.Test;

public class ClassNotFoundExceptionUnitTest {

   @Test(expected = ClassNotFoundException.class)
   public void givenNoDriversInClassPath_whenLoadDrivers_thenClassNotFoundException() throws ClassNotFoundException {
      Class.forName("oracle.jdbc.driver.OracleDriver");
   }
}