package cn.tuyucheng.taketoday.java.list;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ListJUnitTest {

   private final List<String> list1 = Arrays.asList("1", "2", "3", "4");
   private final List<String> list2 = Arrays.asList("1", "2", "3", "4");
   private final List<String> list3 = Arrays.asList("1", "2", "4", "3");

   @Test
   public void whenTestingForEquality_ShouldBeEqual() throws Exception {
      Assertions.assertEquals(list1, list2);
      Assertions.assertNotSame(list1, list2);
      Assertions.assertNotEquals(list1, list3);
   }

   @Test
   public void whenIntersection_ShouldReturnCommonElements() throws Exception {
      List<String> list = Arrays.asList("red", "blue", "blue", "green", "red");
      List<String> otherList = Arrays.asList("red", "green", "green", "yellow");

      Set<String> commonElements = new HashSet(Arrays.asList("red", "green"));

      Set<String> result = list.stream()
            .distinct()
            .filter(otherList::contains)
            .collect(Collectors.toSet());

      Assertions.assertEquals(commonElements, result);

      Set<String> inverseResult = otherList.stream()
            .distinct()
            .filter(list::contains)
            .collect(Collectors.toSet());

      Assertions.assertEquals(commonElements, inverseResult);
   }
}
