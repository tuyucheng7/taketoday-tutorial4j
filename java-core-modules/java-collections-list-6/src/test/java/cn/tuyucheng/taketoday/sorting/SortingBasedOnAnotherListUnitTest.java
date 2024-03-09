package cn.tuyucheng.taketoday.sorting;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static cn.tuyucheng.taketoday.sorting.SortingBasedOnAnotherList.sortUsingComparator;
import static cn.tuyucheng.taketoday.sorting.SortingBasedOnAnotherList.sortUsingForLoop;
import static cn.tuyucheng.taketoday.sorting.SortingBasedOnAnotherList.sortUsingGuava;
import static cn.tuyucheng.taketoday.sorting.SortingBasedOnAnotherList.sortUsingMap;
import static cn.tuyucheng.taketoday.sorting.SortingBasedOnAnotherList.sortUsingStreamAPI;
import static cn.tuyucheng.taketoday.sorting.SortingBasedOnAnotherList.sortUsingVavr;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SortingBasedOnAnotherListUnitTest {
   @Test
   public void givenTwoList_whenUsingForLoop_thenSort() {
      List<String> listWithOrder = Arrays.asList("Burger", "Coke", "Fries", "Pizza");
      List<String> listToSort = Arrays.asList("Pizza", "Burger", "Fries", "Coke");
      sortUsingForLoop(listToSort, listWithOrder);
      List<String> expectedSortedList = Arrays.asList("Burger", "Coke", "Fries", "Pizza");
      assertEquals(expectedSortedList, listWithOrder);
   }

   @Test
   public void givenTwoList_whenUsingComparator_thenSort() {
      List<String> listWithOrder = Arrays.asList("Burger", "Coke", "Fries", "Pizza");
      List<String> listToSort = Arrays.asList("Pizza", "Burger", "Fries", "Coke");
      sortUsingComparator(listToSort, listWithOrder);
      List<String> expectedSortedList = Arrays.asList("Burger", "Coke", "Fries", "Pizza");
      assertEquals(expectedSortedList, listToSort);
   }

   @Test
   public void givenTwoList_whenUsingStreamAPI_thenSort() {
      List<String> listWithOrder = Arrays.asList("Burger", "Coke", "Fries", "Pizza");
      List<String> listToSort = Arrays.asList("Pizza", "Burger", "Fries", "Coke");
      sortUsingStreamAPI(listToSort, listWithOrder);
      List<String> expectedSortedList = Arrays.asList("Burger", "Coke", "Fries", "Pizza");
      assertEquals(expectedSortedList, listToSort);
   }

   @Test
   public void givenTwoList_whenUsingMap_thenSort() {
      List<String> listWithOrder = Arrays.asList("Burger", "Coke", "Fries", "Pizza");
      List<String> listToSort = Arrays.asList("Pizza", "Burger", "Fries", "Coke");
      sortUsingMap(listToSort, listWithOrder);
      List<String> expectedSortedList = Arrays.asList("Burger", "Coke", "Fries", "Pizza");
      assertEquals(expectedSortedList, listToSort);
   }

   @Test
   public void givenTwoList_whenUsingGuavaExplicit_thenSort() {
      List<String> listWithOrder = Arrays.asList("Burger", "Coke", "Fries", "Pizza");
      List<String> listToSort = Arrays.asList("Pizza", "Burger", "Fries", "Coke");
      sortUsingGuava(listToSort, listWithOrder);
      List<String> expectedSortedList = Arrays.asList("Burger", "Coke", "Fries", "Pizza");
      assertEquals(expectedSortedList, listWithOrder);
   }

   @Test
   public void givenTwoList_whenUsingVavr_thenSort() {
      List<String> listWithOrder = Arrays.asList("Burger", "Coke", "Fries", "Pizza");
      List<String> listToSort = Arrays.asList("Pizza", "Burger", "Fries", "Coke");
      sortUsingVavr(listToSort, listWithOrder);
      List<String> expectedSortedList = Arrays.asList("Burger", "Coke", "Fries", "Pizza");
      assertEquals(expectedSortedList, listWithOrder);
   }
}