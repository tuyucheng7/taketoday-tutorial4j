package cn.tuyucheng.taketoday.param;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@DisplayName("Testing PersonValidator")
class PersonValidatorUnitTest {

   /**
    * Nested class, uses ExtendWith
    * {@link cn.tuyucheng.taketoday.param.ValidPersonParameterResolver ValidPersonParameterResolver}
    * to feed Test methods with "good" data.
    */
   @Nested
   @DisplayName("When using Valid data")
   @ExtendWith(ValidPersonParameterResolver.class)
   class ValidData {

      /**
       * Repeat the test ten times, that way we have a good shot at
       * running all of the data through at least once.
       *
       * @param person A valid Person object to validate.
       */
      @RepeatedTest(value = 10)
      @DisplayName("All first names are valid")
      void validateFirstName(Person person) {
         try {
            assertTrue(PersonValidator.validateFirstName(person));
         } catch (PersonValidator.ValidationException e) {
            fail("Exception not expected: " + e.getLocalizedMessage());
         }
      }

      /**
       * Repeat the test ten times, that way we have a good shot at
       * running all of the data through at least once.
       *
       * @param person A valid Person object to validate.
       */
      @RepeatedTest(value = 10)
      @DisplayName("All last names are valid")
      void validateLastName(Person person) {
         try {
            assertTrue(PersonValidator.validateLastName(person));
         } catch (PersonValidator.ValidationException e) {
            fail("Exception not expected: " + e.getLocalizedMessage());
         }
      }
   }

   /**
    * Nested class, uses ExtendWith
    * {@link cn.tuyucheng.taketoday.param.InvalidPersonParameterResolver InvalidPersonParameterResolver}
    * to feed Test methods with "bad" data.
    */
   @Nested
   @DisplayName("When using Invalid data")
   @ExtendWith(InvalidPersonParameterResolver.class)
   class InvalidData {

      /**
       * Repeat the test ten times, that way we have a good shot at
       * running all of the data through at least once.
       *
       * @param person An invalid Person object to validate.
       */
      @RepeatedTest(value = 10)
      @DisplayName("All first names are invalid")
      void validateFirstName(Person person) {
         assertThrows(PersonValidator.ValidationException.class, () -> PersonValidator.validateFirstName(person));
      }

      /**
       * Repeat the test ten times, that way we have a good shot at
       * running all the data through at least once.
       *
       * @param person An invalid Person object to validate.
       */
      @RepeatedTest(value = 10)
      @DisplayName("All first names are invalid")
      void validateLastName(Person person) {
         assertThrows(PersonValidator.ValidationException.class, () -> PersonValidator.validateLastName(person));
      }
   }
}