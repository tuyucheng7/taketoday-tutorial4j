package cn.tuyucheng.taketoday.optional;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OptionalUnitTest {

   private static final String OPTIONAL_RETURN_VALUE = "optionalReturnValue";

   @Test
   void givenOptionalWithValue_whenAssertThatIsNotEmpty_thenOk() {
      assertThat(Optional.of(OPTIONAL_RETURN_VALUE)).isPresent();
   }

   @Test
   void givenOptionalWithValue_whenAssertThatHasValue_thenOk() {
      assertThat(Optional.of(OPTIONAL_RETURN_VALUE)).hasValue(OPTIONAL_RETURN_VALUE);
   }

   @Test
   void givenOptionalWithValue_whenAssertEqualsOptionalObject_thenOk() {
      Optional<String> expected = Optional.of(OPTIONAL_RETURN_VALUE);
      Optional<String> actual = Optional.of(OPTIONAL_RETURN_VALUE);
      assertEquals(expected, actual);
   }

   @Test
   void givenOptionalWithValue_whenAssertEqualsGet_thenOk() {
      Optional<String> optional = Optional.of(OPTIONAL_RETURN_VALUE);
      assertEquals(OPTIONAL_RETURN_VALUE, optional.get());
   }

   @Test
   void givenOptionalWithValue_whenIsPresentAndGetSplit_thenOk() {
      Optional<String> optional = Optional.of(OPTIONAL_RETURN_VALUE);

      assertTrue(optional.isPresent());
      assertEquals(OPTIONAL_RETURN_VALUE, optional.get());
   }

   @Test
   void givenEmptyOptional_whenAssertThatIsEmpty_thenOk() {
      Optional<String> emptyOptional = Optional.empty();
      assertThat(emptyOptional).isEmpty();
   }

   @Test
   void givenEmptyOptional_whenAssertIsNotPresent_thenOk() {
      Optional<String> emptyOptional = Optional.empty();
      assertFalse(emptyOptional.isPresent());
   }
}