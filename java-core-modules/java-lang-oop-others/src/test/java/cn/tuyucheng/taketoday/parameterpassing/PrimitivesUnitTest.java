package cn.tuyucheng.taketoday.parameterpassing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PrimitivesUnitTest {
   @Test
   public void whenModifyingPrimitives_thenOriginalValuesNotModified() {

      int x = 1;
      int y = 2;

      // Before Modification
      Assertions.assertEquals(x, 1);
      Assertions.assertEquals(y, 2);

      modify(x, y);

      // After Modification
      Assertions.assertEquals(x, 1);
      Assertions.assertEquals(y, 2);
   }

   public static void modify(int x1, int y1) {
      x1 = 5;
      y1 = 10;
   }
}
