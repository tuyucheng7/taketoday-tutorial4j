package cn.tuyucheng.taketoday.date.validation;

import org.junit.Test;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DateValidatorUsingDateTimeFormatterUnitTest {

    @Test
    public void givenValidator_whenValidDatePassed_ThenTrue() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.US)
              .withResolverStyle(ResolverStyle.STRICT);
        DateValidator validator = new DateValidatorUsingDateTimeFormatter(dateFormatter);

        assertTrue(validator.isValid("2019-02-28"));
    }

    @Test
    public void givenValidator_whenInValidDatePassed_ThenFalse() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.US)
              .withResolverStyle(ResolverStyle.STRICT);
        DateValidator validator = new DateValidatorUsingDateTimeFormatter(dateFormatter);

        assertFalse(validator.isValid("2019-02-30"));
    }

}
