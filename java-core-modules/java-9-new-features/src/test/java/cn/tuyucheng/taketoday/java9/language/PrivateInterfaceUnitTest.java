package cn.tuyucheng.taketoday.java9.language;

import org.junit.jupiter.api.Test;

public class PrivateInterfaceUnitTest {

   @Test
   public void test() {
      PrivateInterface piClass = new PrivateInterface() {
      };
      piClass.check();
   }

}
