package cn.tuyucheng.taketoday.testing.easymock;

import java.math.BigDecimal;

public class Location {
   private String name;
   private BigDecimal minimumTemperature;
   private BigDecimal maximumTemperature;

   public Location(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public BigDecimal getMinimumTemperature() {
      return minimumTemperature;
   }

   public void setMinimumTemperature(BigDecimal minimumTemperature) {
      this.minimumTemperature = minimumTemperature;
   }

   public BigDecimal getMaximumTemperature() {
      return maximumTemperature;
   }

   public void setMaximumTemperature(BigDecimal maximumTemperature) {
      this.maximumTemperature = maximumTemperature;
   }
}