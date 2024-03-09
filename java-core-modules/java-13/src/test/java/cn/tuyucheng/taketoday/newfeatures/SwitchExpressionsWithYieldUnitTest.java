package cn.tuyucheng.taketoday.newfeatures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SwitchExpressionsWithYieldUnitTest {

   @Test
   public void whenSwitchingOnOperationSquareMe_thenWillReturnSquare() {
      var me = 4;
      var operation = "squareMe";
      var result = switch (operation) {
         case "doubleMe" -> {
            yield me * 2;
         }
         case "squareMe" -> {
            yield me * me;
         }
         default -> me;
      };

      assertEquals(16, result);
   }

}
