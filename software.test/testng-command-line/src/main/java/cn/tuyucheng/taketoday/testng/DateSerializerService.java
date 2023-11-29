package cn.tuyucheng.taketoday.testng;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateSerializerService {
   public String serializeDate(Date date, String format) {
      SimpleDateFormat dateFormat = new SimpleDateFormat(format);
      return dateFormat.format(date);
   }
}