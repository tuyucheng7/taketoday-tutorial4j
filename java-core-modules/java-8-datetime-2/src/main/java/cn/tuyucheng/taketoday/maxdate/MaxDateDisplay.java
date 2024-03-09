package cn.tuyucheng.taketoday.maxdate;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MaxDateDisplay {
   public String getMaxDateValue() {
      Date maxDate = new Date(Long.MAX_VALUE);
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
      return "The maximum date value in Java is: " + sdf.format(maxDate);
   }

   public static void main(String[] args) {
      MaxDateDisplay display = new MaxDateDisplay();
      System.out.println(display.getMaxDateValue());
   }
}

