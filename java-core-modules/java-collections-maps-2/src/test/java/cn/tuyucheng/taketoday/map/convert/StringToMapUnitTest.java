package cn.tuyucheng.taketoday.map.convert;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class StringToMapUnitTest {

   @Test
   public void givenString_WhenUsingStream_ThenResultingStringIsCorrect() {
      Map<String, String> wordsByKey = StringToMap.convertWithStream("1=one,2=two,3=three,4=four");
      Assertions.assertEquals(4, wordsByKey.size());
      Assertions.assertEquals("one", wordsByKey.get("1"));
   }

   @Test
   void givenString_WhenUsingGuava_ThenResultingStringIsCorrect() {
      Map<String, String> wordsByKey = StringToMap.convertWithGuava("1=one,2=two,3=three,4=four");
      Assertions.assertEquals(4, wordsByKey.size());
      Assertions.assertEquals("one", wordsByKey.get("1"));
   }
}