package cn.tuyucheng.taketoday.datafaker;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegexifyUnitTest {

   @Test
   void whenGettingMethidExpression_thenResultNotBlankAndMatchesRegex() {
      assertThat(Regexify.getMethodExpression()).isNotBlank()
            .matches("[A-D]{4,10}");
   }

   @Test
   void whenGettingExpression_thenResultNotBlankAndMatchesRegex() {
      assertThat(Regexify.getExpression()).isNotBlank()
            .matches("(hello|bye|hey)");
   }
}