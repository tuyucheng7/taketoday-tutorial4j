package cn.tuyucheng.taketoday.assertj.exceptions;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

class Java8StyleAssertions {

   @Test
   void whenGettingOutOfBoundsItem_thenIndexOutOfBoundsException() {
      assertThatThrownBy(() -> {
         ArrayList<String> myStringList = new ArrayList<>(Arrays.asList("String one", "String two"));
         myStringList.get(2);
      }).isInstanceOf(IndexOutOfBoundsException.class)
            .hasMessageStartingWith("Index 2")
            .hasMessageContaining("2")
            .hasMessageEndingWith("length 2")
            .hasMessageContaining("Index 2", "length 2")
            .hasMessage("Index 2 out of bounds for length 2")
            // .hasMessage("Index %s, length %s", 2, 2)
            // .hasMessageMatching("Index \\d+, length \\d+")
            .hasNoCause();
   }

   @Test
   void whenWrappingException_thenCauseInstanceOfWrappedExceptionType() {
      assertThatThrownBy(() -> {
         try {
            throw new IOException();
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }).isInstanceOf(RuntimeException.class)
            .hasCauseInstanceOf(IOException.class)
            .hasStackTraceContaining("IOException");
   }

   @Test
   void whenDividingByZero_thenArithmeticException() {
      assertThatExceptionOfType(ArithmeticException.class).isThrownBy(() -> {
               int numerator = 10;
               int denominator = 0;
               int quotient = numerator / denominator;
            })
            .withMessageContaining("/ by zero");

      // Alternatively:

      // when
      Throwable thrown = catchThrowable(() -> {
         int numerator = 10;
         int denominator = 0;
         int quotient = numerator / denominator;
      });

      // then
      assertThat(thrown).isInstanceOf(ArithmeticException.class)
            .hasMessageContaining("/ by zero");

   }
}