package cn.tuyucheng.taketoday.date.validation;

import org.junit.Test;

import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DateValidatorUsingLocalDateUnitTest {

    @Test
    public void givenValidator_whenValidDatePassed_ThenTrue() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.BASIC_ISO_DATE;
        DateValidator validator = new DateValidatorUsingLocalDate(dateFormatter);

        assertTrue(validator.isValid("20190228"));
    }

    @Test
    public void givenValidator_whenInValidDatePassed_ThenFalse() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.BASIC_ISO_DATE;
        DateValidator validator = new DateValidatorUsingLocalDate(dateFormatter);

        assertFalse(validator.isValid("20190230"));
    }
}
