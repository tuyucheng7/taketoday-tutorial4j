package cn.tuyucheng.taketoday.date.validation;

import org.apache.commons.validator.GenericValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DateValidatorUsingApacheValidatorUnitTest {

   @Test
   public void whenValidDatePassed_ThenTrue() {
      assertTrue(GenericValidator.isDate("2019-02-28", "yyyy-MM-dd", true));
   }

   @Test
   public void whenInvalidDatePassed_ThenFalse() {
      assertFalse(GenericValidator.isDate("2019-02-29", "yyyy-MM-dd", true));
   }
}
