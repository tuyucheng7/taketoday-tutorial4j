package cn.tuyucheng.taketoday.stringIterator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringIteratorTest {

   @Test
   public void whenUseCharArrayMethod_thenIterate() {
      String input = "Hello, Tuyucheng!";
      String expectedOutput = "Hello, Tuyucheng!";
      String result = StringIterator.javaCharArray(input);
      assertEquals(expectedOutput, result);
   }

   @Test
   public void whenUseJavaForLoop_thenIterate() {
      String input = "Hello, Tuyucheng!";
      String expectedOutput = "Hello, Tuyucheng!";
      String result = StringIterator.javaforLoop(input);
      assertEquals(expectedOutput, result);
   }

   @Test
   public void whenUseForEachMethod_thenIterate() {
      String input = "Hello, Tuyucheng!";
      String expectedOutput = "Hello, Tuyucheng!";
      String result = StringIterator.java8forEach(input);
      assertEquals(expectedOutput, result);
   }

   @Test
   public void whenUseCharacterIterator_thenIterate() {
      String input = "Hello, Tuyucheng!";
      String expectedOutput = "Hello, Tuyucheng!";
      String result = StringIterator.javaCharacterIterator(input);
      assertEquals(expectedOutput, result);
   }
}
