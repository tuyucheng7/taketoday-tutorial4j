package cn.tuyucheng.taketoday.junit5vstestng;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CustomNameUnitTest {

   @ParameterizedTest
   @ValueSource(strings = {"Hello", "World"})
   @DisplayName("Test Method to check that the inputs are not nullable")
   void givenString_TestNullOrNot(String word) {
      assertNotNull(word);
   }
}