package cn.tuyucheng.taketoday.testing.easymock;

import java.math.BigDecimal;

public class ForecastProcessor {
   private WeatherService weatherService;

   public BigDecimal getMaximumTemperature(String locationName) {
      Location location = new Location(locationName);

      try {
         weatherService.populateTemperature(location);
      } catch (ServiceUnavailableException e) {
         return null;
      }

      return location.getMaximumTemperature();
   }

   public WeatherService getWeatherService() {
      return weatherService;
   }

   public void setWeatherService(WeatherService weatherService) {
      this.weatherService = weatherService;
   }
}