package cn.tuyucheng.taketoday.removeduplicates;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RemoveDuplicateFromStringUnitTest {

   private final static String STR1 = "racecar";
   private final static String STR2 = "J2ee programming";
   private final static String STR_EMPTY = "";

   private RemoveDuplicateFromString removeDuplicateFromString;

   @BeforeEach
   public void executedBeforeEach() {
      removeDuplicateFromString = new RemoveDuplicateFromString();
   }


   @Test
   public void whenUsingCharArray_DuplicatesShouldBeRemovedWithoutKeepingStringOrder() {
      String str1 = removeDuplicateFromString.removeDuplicatesUsingCharArray(STR1);
      String str2 = removeDuplicateFromString.removeDuplicatesUsingCharArray(STR2);
      String strEmpty = removeDuplicateFromString.removeDuplicatesUsingCharArray(STR_EMPTY);
      Assertions.assertEquals("", strEmpty);
      Assertions.assertEquals("ecar", str1);
      Assertions.assertEquals("J2e poraming", str2);
   }

   @Test
   public void whenUsingLinkedHashSet_DuplicatesShouldBeRemovedAndItKeepStringOrder() {
      String str1 = removeDuplicateFromString.removeDuplicatesUsinglinkedHashSet(STR1);
      String str2 = removeDuplicateFromString.removeDuplicatesUsinglinkedHashSet(STR2);

      String strEmpty = removeDuplicateFromString.removeDuplicatesUsinglinkedHashSet(STR_EMPTY);
      Assertions.assertEquals("", strEmpty);
      Assertions.assertEquals("race", str1);
      Assertions.assertEquals("J2e progamin", str2);
   }

   @Test
   public void whenUsingSorting_DuplicatesShouldBeRemovedWithoutKeepingStringOrder() {
      String str1 = removeDuplicateFromString.removeDuplicatesUsingSorting(STR1);
      String str2 = removeDuplicateFromString.removeDuplicatesUsingSorting(STR2);

      String strEmpty = removeDuplicateFromString.removeDuplicatesUsingSorting(STR_EMPTY);
      Assertions.assertEquals("", strEmpty);
      Assertions.assertEquals("acer", str1);
      Assertions.assertEquals(" 2Jaegimnopr", str2);
   }

   @Test
   public void whenUsingHashSet_DuplicatesShouldBeRemovedWithoutKeepingStringOrder() {
      String str1 = removeDuplicateFromString.removeDuplicatesUsingHashSet(STR1);
      String str2 = removeDuplicateFromString.removeDuplicatesUsingHashSet(STR2);
      String strEmpty = removeDuplicateFromString.removeDuplicatesUsingHashSet(STR_EMPTY);
      Assertions.assertEquals("", strEmpty);
      Assertions.assertEquals("arce", str1);
      Assertions.assertEquals(" pa2regiJmno", str2);
   }

   @Test
   public void whenUsingIndexOf_DuplicatesShouldBeRemovedWithoutKeepingStringOrder() {
      String str1 = removeDuplicateFromString.removeDuplicatesUsingIndexOf(STR1);
      String str2 = removeDuplicateFromString.removeDuplicatesUsingIndexOf(STR2);
      String strEmpty = removeDuplicateFromString.removeDuplicatesUsingIndexOf(STR_EMPTY);
      Assertions.assertEquals("", strEmpty);
      Assertions.assertEquals("ecar", str1);
      Assertions.assertEquals("J2e poraming", str2);
   }

   @Test
   public void whenUsingJava8_DuplicatesShouldBeRemovedAndItKeepStringOrder() {
      String str1 = removeDuplicateFromString.removeDuplicatesUsingDistinct(STR1);
      String str2 = removeDuplicateFromString.removeDuplicatesUsingDistinct(STR2);
      String strEmpty = removeDuplicateFromString.removeDuplicatesUsingDistinct(STR_EMPTY);
      Assertions.assertEquals("", strEmpty);
      Assertions.assertEquals("race", str1);
      Assertions.assertEquals("J2e progamin", str2);
   }
}
