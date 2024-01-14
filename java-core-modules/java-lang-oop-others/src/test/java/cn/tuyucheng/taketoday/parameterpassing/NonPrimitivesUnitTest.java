package cn.tuyucheng.taketoday.parameterpassing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NonPrimitivesUnitTest {
   @Test
   public void whenModifyingObjects_thenOriginalObjectChanged() {
      Foo a = new Foo(1);
      Foo b = new Foo(1);

      // Before Modification
      Assertions.assertEquals(a.num, 1);
      Assertions.assertEquals(b.num, 1);

      modify(a, b);

      // After Modification
      Assertions.assertEquals(a.num, 2);
      Assertions.assertEquals(b.num, 1);
   }

   public static void modify(Foo a1, Foo b1) {
      a1.num++;

      b1 = new Foo(1);
      b1.num++;
   }
}

class Foo {
   public int num;

   public Foo(int num) {
      this.num = num;
   }
}