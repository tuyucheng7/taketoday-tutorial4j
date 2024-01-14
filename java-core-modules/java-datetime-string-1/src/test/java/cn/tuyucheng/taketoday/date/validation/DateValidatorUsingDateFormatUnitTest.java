package cn.tuyucheng.taketoday.date.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DateValidatorUsingDateFormatUnitTest {

   @Test
   public void givenValidator_whenValidDatePassed_ThenTrue() {
      DateValidator validator = new DateValidatorUsingDateFormat("MM/dd/yyyy");

      assertTrue(validator.isValid("02/28/2019"));
   }

   @Test
   public void givenValidator_whenInvalidDatePassed_ThenFalse() {
      DateValidator validator = new DateValidatorUsingDateFormat("MM/dd/yyyy");

      assertFalse(validator.isValid("02/30/2019"));
   }
}
