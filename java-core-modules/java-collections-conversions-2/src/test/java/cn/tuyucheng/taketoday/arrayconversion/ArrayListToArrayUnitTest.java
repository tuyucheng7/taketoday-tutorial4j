package cn.tuyucheng.taketoday.arrayconversion;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ArrayListToArrayUnitTest {
   private static final List<String> INPUT_LIST = Lists.newArrayList("Michael Bolton", "Michael Jackson", "Guns and Roses", "Bryan Adams", "Air Supply");
   private static final String[] EXPECTED_ARRAY = new String[]{"Michael Bolton", "Michael Jackson", "Guns and Roses", "Bryan Adams", "Air Supply"};

   @Test
   void whenUsingForLoop_thenGetExpectedResult() {
      String[] result = new String[INPUT_LIST.size()];
      for (int i = 0; i < INPUT_LIST.size(); i++) {
         result[i] = INPUT_LIST.get(i);
      }
      assertArrayEquals(EXPECTED_ARRAY, result);
   }

   @Test
   void whenUsingListToArray_thenGetExpectedResult() {
      String[] result = new String[INPUT_LIST.size()];
      INPUT_LIST.toArray(result);
      assertArrayEquals(EXPECTED_ARRAY, result);

      String[] result2 = INPUT_LIST.toArray(new String[0]);
      assertArrayEquals(EXPECTED_ARRAY, result2);
   }

   @Test
   void whenUsingStreamApi_thenGetExpectedResult() {
      String[] result = INPUT_LIST.stream()
            .toArray(String[]::new);
      assertArrayEquals(EXPECTED_ARRAY, result);
   }

   @Test
   void whenUsingListToArrayInJava11_thenGetExpectedResult() {
      String[] result = INPUT_LIST.toArray(String[]::new);
      assertArrayEquals(EXPECTED_ARRAY, result);
   }
}