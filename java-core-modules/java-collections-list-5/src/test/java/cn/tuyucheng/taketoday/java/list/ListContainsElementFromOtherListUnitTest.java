package cn.tuyucheng.taketoday.java.list;

import cn.tuyucheng.taketoday.list.Country;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ListContainsElementFromOtherListUnitTest {

   final private List<String> listOfLetters = Arrays.asList("a", "b", "c", "d");
   final private List<String> listOfLettersWithOverlap = Arrays.asList("d", "e", "f", "g");
   final private List<String> listOfCities = Arrays.asList("London", "Berlin", "Paris", "Brussels");

   @Test
   public void givenValuesToCompare_whenUsingCollectionsDisjoint_thenDetectElementsInTwoLists() {
      boolean shouldBeTrue = !Collections.disjoint(listOfLetters, listOfLettersWithOverlap);
      assertTrue(shouldBeTrue);

      boolean shouldBeFalse = !Collections.disjoint(listOfLetters, listOfCities);
      assertFalse(shouldBeFalse);
   }

   @Test
   public void givenValuesToCompare_whenUsingStreams_thenDetectElementsInTwoLists() {
      boolean shouldBeTrue = listOfLetters.stream()
            .anyMatch(listOfLettersWithOverlap::contains);
      assertTrue(shouldBeTrue);

      boolean shouldBeFalse = listOfLetters.stream()
            .anyMatch(listOfCities::contains);
      assertFalse(shouldBeFalse);
   }

   @Test
   public void givenValuesToCompare_whenUsingApacheCollectionUtils_thenDetectElementsInTwoLists() {
      boolean shouldBeTrue = CollectionUtils.containsAny(listOfLetters, listOfLettersWithOverlap);
      assertTrue(shouldBeTrue);

      boolean shouldBeFalse = CollectionUtils.containsAny(listOfLetters, listOfCities);
      assertFalse(shouldBeFalse);
   }

   @Test
   public void givenPropertiesInObjectsToCompare_whenUsingStreams_thenDetectElementsInTwoLists() {
      Country france = new Country("France", "French");
      Country mexico = new Country("Mexico", "Spanish");
      Country spain = new Country("Spain", "Spanish");
      List<Country> franceAndMexico = Arrays.asList(france, mexico);
      List<Country> franceAndSpain = Arrays.asList(france, spain);

      boolean shouldBeTrue = franceAndMexico.stream()
            .map(Country::getLanguage)
            .anyMatch(franceAndSpain.stream()
                  .map(Country::getLanguage)
                  .collect(toSet())::contains);

      assertTrue(shouldBeTrue);
   }

}