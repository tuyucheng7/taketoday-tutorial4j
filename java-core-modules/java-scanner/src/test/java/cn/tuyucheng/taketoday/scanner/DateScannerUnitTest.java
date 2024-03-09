package cn.tuyucheng.taketoday.scanner;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateScannerUnitTest {

   @Test
   void whenScanToLocalDate_ThenCorrectLocalDate() {
      String dateString = "2018-09-09";
      Assertions.assertEquals(LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd")), new DateScanner().scanToLocalDate(dateString));
   }

   @Test
   void whenScanToDate_ThenCorrectDate() throws ParseException {
      String dateString = "2018-09-09";
      assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse(dateString), new DateScanner().scanToDate(dateString));
   }

}
