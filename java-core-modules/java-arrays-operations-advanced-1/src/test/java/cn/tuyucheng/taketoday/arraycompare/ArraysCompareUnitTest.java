package cn.tuyucheng.taketoday.arraycompare;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ArraysCompareUnitTest {

   @Test
   void givenSameContents_whenCompare_thenCorrect() {
      String[] array1 = new String[]{"A", "B", "C"};
      String[] array2 = new String[]{"A", "B", "C"};

      assertThat(Arrays.compare(array1, array2)).isEqualTo(0);
   }

   @Test
   void givenDifferentContents_whenCompare_thenDifferent() {
      String[] array1 = new String[]{"A", "B", "C"};
      String[] array2 = new String[]{"A", "C", "B", "D"};

      assertThat(Arrays.compare(array1, array2)).isLessThan(0);
      assertThat(Arrays.compare(array2, array1)).isGreaterThan(0);
      assertThat(Arrays.compare(array1, null)).isGreaterThan(0);
   }
}