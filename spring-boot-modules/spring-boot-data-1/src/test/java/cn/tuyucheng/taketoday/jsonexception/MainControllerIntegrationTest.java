package cn.tuyucheng.taketoday.jsonexception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MainControllerIntegrationTest {

   @Test
   void givenIndex_thenCustomException() throws CustomException {
      MainController mainController = new MainController();

      assertThrows(CustomException.class, mainController::index);
   }
}