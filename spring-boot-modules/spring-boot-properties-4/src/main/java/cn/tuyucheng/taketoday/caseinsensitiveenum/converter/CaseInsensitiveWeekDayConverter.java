package cn.tuyucheng.taketoday.caseinsensitiveenum.converter;

import cn.tuyucheng.taketoday.caseinsensitiveenum.week.WeekDays;
import org.springframework.core.convert.converter.Converter;

public class CaseInsensitiveWeekDayConverter implements Converter<String, WeekDays> {

   @Override
   public WeekDays convert(final String source) {
      try {
         return WeekDays.valueOf(source.trim());
      } catch (IllegalArgumentException exception) {
         return WeekDays.valueOf(source.trim().toUpperCase());
      }
   }
}