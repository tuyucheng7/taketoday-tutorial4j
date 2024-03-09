package cn.tuyucheng.taketoday.methodoverloadingoverriding;

import cn.tuyucheng.taketoday.methodoverloadingoverriding.util.Multiplier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MethodOverloadingUnitTest {

   private static Multiplier multiplier;

   @BeforeAll
   public static void setUpMultiplierInstance() {
      multiplier = new Multiplier();
   }

   @Test
   public void givenMultiplierInstance_whenCalledMultiplyWithTwoIntegers_thenOneAssertion() {
      assertThat(multiplier.multiply(10, 10)).isEqualTo(100);
   }

   @Test
   public void givenMultiplierInstance_whenCalledMultiplyWithTreeIntegers_thenOneAssertion() {
      assertThat(multiplier.multiply(10, 10, 10)).isEqualTo(1000);
   }

   @Test
   public void givenMultiplierInstance_whenCalledMultiplyWithIntDouble_thenOneAssertion() {
      assertThat(multiplier.multiply(10, 10.5)).isEqualTo(105.0);
   }

   @Test
   public void givenMultiplierInstance_whenCalledMultiplyWithDoubleDouble_thenOneAssertion() {
      assertThat(multiplier.multiply(10.5, 10.5)).isEqualTo(110.25);
   }

   @Test
   public void givenMultiplierInstance_whenCalledMultiplyWithIntIntAndMatching_thenNoTypePromotion() {
      assertThat(multiplier.multiply(10, 10)).isEqualTo(100);
   }
}
