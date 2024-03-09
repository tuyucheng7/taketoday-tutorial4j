package cn.tuyucheng.taketoday.listduplicate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class ListDuplicateUnitTest {
   private static ListDuplicate listDuplicate;

   @BeforeAll
   public static void setup() {
      listDuplicate = new ListDuplicate();
   }

   @Test
   public void givenList_whenUsingSet_thenReturnDuplicateElements() {
      List<Integer> list = Arrays.asList(1, 2, 3, 3, 4, 4, 5);
      List<Integer> duplicates = listDuplicate.listDuplicateUsingSet(list);
      Assertions.assertEquals(duplicates.size(), 2);
      Assertions.assertEquals(duplicates.contains(3), true);
      Assertions.assertEquals(duplicates.contains(4), true);
      Assertions.assertEquals(duplicates.contains(1), false);
   }

   @Test
   public void givenList_whenUsingFrequencyMap_thenReturnDuplicateElements() {
      List<Integer> list = Arrays.asList(1, 2, 3, 3, 4, 4, 5);
      List<Integer> duplicates = listDuplicate.listDuplicateUsingMap(list);
      Assertions.assertEquals(duplicates.size(), 2);
      Assertions.assertEquals(duplicates.contains(3), true);
      Assertions.assertEquals(duplicates.contains(4), true);
      Assertions.assertEquals(duplicates.contains(1), false);
   }

   @Test
   public void givenList_whenUsingFilterAndSetAdd_thenReturnDuplicateElements() {
      List<Integer> list = Arrays.asList(1, 2, 3, 3, 4, 4, 5);
      List<Integer> duplicates = listDuplicate.listDuplicateUsingFilterAndSetAdd(list);
      Assertions.assertEquals(duplicates.size(), 2);
      Assertions.assertEquals(duplicates.contains(3), true);
      Assertions.assertEquals(duplicates.contains(4), true);
      Assertions.assertEquals(duplicates.contains(1), false);
   }

   @Test
   public void givenList_whenUsingCollectionsFrequency_thenReturnDuplicateElements() {
      List<Integer> list = Arrays.asList(1, 2, 3, 3, 4, 4, 5);
      List<Integer> duplicates = listDuplicate.listDuplicateUsingCollectionsFrequency(list);
      Assertions.assertEquals(duplicates.size(), 2);
      Assertions.assertEquals(duplicates.contains(3), true);
      Assertions.assertEquals(duplicates.contains(4), true);
      Assertions.assertEquals(duplicates.contains(1), false);
   }

   @Test
   public void givenList_whenUsingMapAndCollectorsGroupingBy_thenReturnDuplicateElements() {
      List<Integer> list = Arrays.asList(1, 2, 3, 3, 4, 4, 5);
      List<Integer> duplicates = listDuplicate.listDuplicateUsingCollectionsFrequency(list);
      Assertions.assertEquals(duplicates.size(), 2);
      Assertions.assertEquals(duplicates.contains(3), true);
      Assertions.assertEquals(duplicates.contains(4), true);
      Assertions.assertEquals(duplicates.contains(1), false);
   }

}
