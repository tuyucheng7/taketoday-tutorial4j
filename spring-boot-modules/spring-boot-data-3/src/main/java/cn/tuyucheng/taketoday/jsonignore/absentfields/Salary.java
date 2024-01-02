package cn.tuyucheng.taketoday.jsonignore.absentfields;

import java.math.BigDecimal;

public class Salary {

   private BigDecimal hourlyRate;

   public Salary() {
   }

   public Salary(BigDecimal hourlyRate) {
      this.hourlyRate = hourlyRate;
   }

   public BigDecimal getHourlyRate() {
      return hourlyRate;
   }

   public void setHourlyRate(final BigDecimal hourlyRate) {
      this.hourlyRate = hourlyRate;
   }

   @Override
   public String toString() {
      return STR."Salary{hourlyRate=\{hourlyRate}\{'}'}";
   }
}