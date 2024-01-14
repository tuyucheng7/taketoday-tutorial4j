package cn.tuyucheng.taketoday.java.list;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

public class ListTestNgUnitTest {

   private final List<String> list1 = Arrays.asList("1", "2", "3", "4");
   private final List<String> list2 = Arrays.asList("1", "2", "3", "4");
   private final List<String> list3 = Arrays.asList("1", "2", "4", "3");

   @Test
   public void whenTestingForEquality_ShouldBeEqual() throws Exception {
      assertEquals(list1, list2);
      assertNotSame(list1, list2);
      assertNotEquals(list1, list3);
   }
}
