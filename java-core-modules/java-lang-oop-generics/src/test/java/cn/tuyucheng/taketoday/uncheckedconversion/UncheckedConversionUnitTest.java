package cn.tuyucheng.taketoday.uncheckedconversion;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class UncheckedConversionUnitTest {

   @Test
   public void givenRawList_whenAssignToTypedList_shouldHaveCompilerWarning() {
      List<String> fromRawList = UncheckedConversion.getRawList();
      Assertions.assertEquals(3, fromRawList.size());
      Assertions.assertEquals("I am the 1st String.", fromRawList.get(0));
   }

   @Test(expected = ClassCastException.class)
   public void givenRawList_whenListHasMixedType_shouldThrowClassCastException() {
      List<String> fromRawList = UncheckedConversion.getRawListWithMixedTypes();
      Assertions.assertEquals(4, fromRawList.size());
      Assertions.assertFalse(fromRawList.get(3).endsWith("String."));
   }

   @Test
   public void givenRawList_whenAssignToTypedListAfterCallingCastList_shouldOnlyHaveElementsWithExpectedType() {
      List rawList = UncheckedConversion.getRawListWithMixedTypes();
      List<String> strList = UncheckedConversion.castList(String.class, rawList);
      Assertions.assertEquals(4, rawList.size());
      Assertions.assertEquals("One element with the wrong type has been filtered out.", 3, strList.size());
      Assertions.assertTrue(strList.stream().allMatch(el -> el.endsWith("String.")));
   }

   @Test(expected = ClassCastException.class)
   public void givenRawListWithWrongType_whenAssignToTypedListAfterCallingCastList2_shouldThrowException() {
      List rawList = UncheckedConversion.getRawListWithMixedTypes();
      UncheckedConversion.castList2(String.class, rawList);
   }

}
