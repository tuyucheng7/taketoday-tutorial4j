package cn.tuyucheng.taketoday.equalshashcode.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;

public class SquareClassUnitTest {

   @Test
   public void testEqualsAndHashcodes() {
      Square aObject = new Square(10, Color.BLUE);
      Square bObject = new Square(10, Color.BLUE);

      Square dObject = new Square(20, Color.BLUE);

      Assertions.assertTrue(aObject.equals(bObject) && bObject.equals(aObject));

      Assertions.assertTrue(aObject.hashCode() == bObject.hashCode());

      Assertions.assertFalse(aObject.equals(dObject));
      Assertions.assertFalse(aObject.hashCode() == dObject.hashCode());
   }

}
