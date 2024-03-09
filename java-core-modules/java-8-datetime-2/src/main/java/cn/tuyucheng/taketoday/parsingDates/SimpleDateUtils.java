package cn.tuyucheng.taketoday.parsingDates;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Date;

public class SimpleDateUtils {

   public static Date parseDate(String date) {
      try {
         return DateUtils.parseDateStrictly(date,
               new String[]{"yyyy/MM/dd", "dd/MM/yyyy", "yyyy-MM-dd"});
      } catch (ParseException ex) {
         return null;
      }
   }

}
