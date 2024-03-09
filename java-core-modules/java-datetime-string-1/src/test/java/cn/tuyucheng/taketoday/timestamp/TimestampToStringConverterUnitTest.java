package cn.tuyucheng.taketoday.timestamp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class TimestampToStringConverterUnitTest {

   @Test
   public void givenDatePattern_whenFormatting_thenResultingStringIsCorrect() {
      Timestamp timestamp = Timestamp.valueOf("2018-12-12 01:02:03.123456789");
      DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

      String timestampAsString = formatter.format(timestamp.toLocalDateTime());
      Assertions.assertEquals("2018-12-12T01:02:03.123456789", timestampAsString);
   }
}