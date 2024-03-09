package cn.tuyucheng.taketoday.equalshashcode.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PrimitiveClassUnitTest {

   @Test
   public void testTwoEqualsObjects() {

      PrimitiveClass aObject = new PrimitiveClass(false, 2);
      PrimitiveClass bObject = new PrimitiveClass(false, 2);
      PrimitiveClass dObject = new PrimitiveClass(true, 2);

      Assertions.assertTrue(aObject.equals(bObject) && bObject.equals(aObject));

      Assertions.assertTrue(aObject.hashCode() == bObject.hashCode());

      Assertions.assertFalse(aObject.equals(dObject));
      Assertions.assertFalse(aObject.hashCode() == dObject.hashCode());
   }

}
