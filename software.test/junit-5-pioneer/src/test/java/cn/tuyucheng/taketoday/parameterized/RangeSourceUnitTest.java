package cn.tuyucheng.taketoday.parameterized;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junitpioneer.jupiter.params.ByteRangeSource;
import org.junitpioneer.jupiter.params.DoubleRangeSource;
import org.junitpioneer.jupiter.params.IntRangeSource;
import org.junitpioneer.jupiter.params.LongRangeSource;

import static org.assertj.core.api.Assertions.assertThat;

public class RangeSourceUnitTest {

   @ParameterizedTest
   @IntRangeSource(from = 0, to = 10)
   void givenRangeSource_thenCorrect(int digit) {
      assertThat(digit).isBetween(0, 10);
   }

   @ParameterizedTest
   @DoubleRangeSource(from = 1, to = 10, step = 2)
   void givenRangeSource_whenStepSizeIsTwo_thenOnlyOddNumbers(double number) {
      assertThat(number % 2).isNotZero();
   }

   @ParameterizedTest
   @Disabled("This test is disabled because using illegal range source")
   @ByteRangeSource(from = 0, to = 0)
   void givenIllegalRangeSource_thenShouldThrowEx(byte arg) {
   }

   @ParameterizedTest
   @LongRangeSource(from = 0L, to = 0L, closed = true)
   void givenLegalRangeSource_thenCorrect(long arg) {
      assertThat(arg).isEqualTo(0L);
   }
}