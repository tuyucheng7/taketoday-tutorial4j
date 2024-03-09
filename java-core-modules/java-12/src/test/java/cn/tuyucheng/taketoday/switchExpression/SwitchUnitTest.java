package cn.tuyucheng.taketoday.switchExpression;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SwitchUnitTest {

   @Test
   public void switchJava12() {

      var month = Month.AUG;

      var value = switch (month) {
         case JAN, JUN, JUL -> 3;
         case FEB, SEP, OCT, NOV, DEC -> 1;
         case MAR, MAY, APR, AUG -> 2;
      };

      Assertions.assertEquals(value, 2);
   }


   enum Month {JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC}
}
