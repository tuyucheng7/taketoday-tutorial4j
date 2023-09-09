package cn.tuyucheng.taketoday.testing.easymock;

public interface WeatherService {
   void populateTemperature(Location location) throws ServiceUnavailableException;
}