package cn.tuyucheng.taketoday.datetime.sql;

import org.junit.Test;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;

public class DateUtilsUnitTest {

    @Test(expected = IllegalArgumentException.class)
    public void givenDateAsString_whenPatternIsNotRespected_thenExceptionIsThrown() {
        DateUtils.getDate("2020 01 01");
    }

    @Test
    public void givenDateAndPattern_thenDateIsCorrectlyReturned() throws ParseException {
        assertEquals(DateUtils.getDate("2020-01-01"), DateUtils.getDate("2020/01/01", "yyyy/MM/dd"));
    }
}