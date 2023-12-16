package cn.tuyucheng.taketoday.java8;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Java8PredicateChainUnitTest {

   private List<String> names = Arrays.asList("Adam", "Alexander", "John", "Tom");

   @Test
   void whenFilterList_thenSuccess() {
      List<String> result = names.stream()
            .filter(name -> name.startsWith("A"))
            .collect(Collectors.toList());

      assertEquals(2, result.size());
      assertThat(result, contains("Adam", "Alexander"));
   }

   @Test
   void whenFilterListWithMultipleFilters_thenSuccess() {
      List<String> result = names.stream()
            .filter(name -> name.startsWith("A"))
            .filter(name -> name.length() < 5)
            .collect(Collectors.toList());

      assertEquals(1, result.size());
      assertThat(result, contains("Adam"));
   }

   @Test
   void whenFilterListWithComplexPredicate_thenSuccess() {
      List<String> result = names.stream()
            .filter(name -> name.startsWith("A") && name.length() < 5)
            .collect(Collectors.toList());

      assertEquals(1, result.size());
      assertThat(result, contains("Adam"));
   }

   @Test
   void whenFilterListWithCombinedPredicatesInline_thenSuccess() {
      List<String> result = names.stream()
            .filter(((Predicate<String>) name -> name.startsWith("A")).and(name -> name.length() < 5))
            .collect(Collectors.toList());

      assertEquals(1, result.size());
      assertThat(result, contains("Adam"));
   }

   @Test
   void whenFilterListWithCombinedPredicatesUsingAnd_thenSuccess() {
      Predicate<String> predicate1 = str -> str.startsWith("A");
      Predicate<String> predicate2 = str -> str.length() < 5;

      List<String> result = names.stream()
            .filter(predicate1.and(predicate2))
            .collect(Collectors.toList());

      assertEquals(1, result.size());
      assertThat(result, contains("Adam"));
   }

   @Test
   void whenFilterListWithCombinedPredicatesUsingOr_thenSuccess() {
      Predicate<String> predicate1 = str -> str.startsWith("J");
      Predicate<String> predicate2 = str -> str.length() < 4;

      List<String> result = names.stream()
            .filter(predicate1.or(predicate2))
            .collect(Collectors.toList());

      assertEquals(2, result.size());
      assertThat(result, contains("John", "Tom"));
   }

   @Test
   void whenFilterListWithCombinedPredicatesUsingOrAndNegate_thenSuccess() {
      Predicate<String> predicate1 = str -> str.startsWith("J");
      Predicate<String> predicate2 = str -> str.length() < 4;

      List<String> result = names.stream()
            .filter(predicate1.or(predicate2.negate()))
            .collect(Collectors.toList());

      assertEquals(3, result.size());
      assertThat(result, contains("Adam", "Alexander", "John"));
   }

   @Test
   void whenFilterListWithCollectionOfPredicatesUsingAnd_thenSuccess() {
      List<Predicate<String>> allPredicates = new ArrayList<Predicate<String>>();
      allPredicates.add(str -> str.startsWith("A"));
      allPredicates.add(str -> str.contains("d"));
      allPredicates.add(str -> str.length() > 4);

      List<String> result = names.stream()
            .filter(allPredicates.stream()
                  .reduce(x -> true, Predicate::and))
            .collect(Collectors.toList());

      assertEquals(1, result.size());
      assertThat(result, contains("Alexander"));
   }

   @Test
   void whenFilterListWithCollectionOfPredicatesUsingOr_thenSuccess() {
      List<Predicate<String>> allPredicates = new ArrayList<Predicate<String>>();
      allPredicates.add(str -> str.startsWith("A"));
      allPredicates.add(str -> str.contains("d"));
      allPredicates.add(str -> str.length() > 4);

      List<String> result = names.stream()
            .filter(allPredicates.stream()
                  .reduce(x -> false, Predicate::or))
            .collect(Collectors.toList());

      assertEquals(2, result.size());
      assertThat(result, contains("Adam", "Alexander"));
   }
}