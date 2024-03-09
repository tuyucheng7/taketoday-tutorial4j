package cn.tuyucheng.taketoday.uncheckedcast;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.Map;

public class UncheckedCastUnitTest {

   @Test
   public void givenRawMap_whenCastToTypedMap_shouldHaveCompilerWarning() {
      Map<String, LocalDate> castFromRawMap = (Map<String, LocalDate>) UncheckedCast.getRawMap();
      Assertions.assertEquals(3, castFromRawMap.size());
      Assertions.assertEquals(castFromRawMap.get("date 2"), LocalDate.of(1992, Month.AUGUST, 8));
   }

   @Test(expected = ClassCastException.class)
   public void givenMixTypedRawMap_whenCastToTypedMap_shouldThrowClassCastException() {
      Map<String, LocalDate> castFromRawMap = (Map<String, LocalDate>) UncheckedCast.getRawMapWithMixedTypes();
      Assertions.assertEquals(4, castFromRawMap.size());
      Assertions.assertTrue(castFromRawMap.get("date 4").isAfter(castFromRawMap.get("date 3")));
   }
}
