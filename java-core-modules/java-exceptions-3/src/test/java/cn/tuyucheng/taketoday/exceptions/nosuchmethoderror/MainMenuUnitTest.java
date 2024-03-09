package cn.tuyucheng.taketoday.exceptions.nosuchmethoderror;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class MainMenuUnitTest {

   @Test
   void whenGetSpecials_thenNotNull() {
      assertNotNull(MainMenu.getSpecials());
   }
}
