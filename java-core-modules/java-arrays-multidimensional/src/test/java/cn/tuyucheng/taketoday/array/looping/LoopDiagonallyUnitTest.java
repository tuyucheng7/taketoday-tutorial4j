package cn.tuyucheng.taketoday.array.looping;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoopDiagonallyUnitTest {

   @Test
   public void twoArrayIsLoopedDiagonallyAsExpected() {

      LoopDiagonally loopDiagonally = new LoopDiagonally();
      String[][] twoDArray = {{"a", "b", "c"},
            {"d", "e", "f"},
            {"g", "h", "i"}};

      String output = loopDiagonally.loopDiagonally(twoDArray);
      assertEquals("a db gec hf i", output);
   }
}