package cn.tuyucheng.taketoday.testing.easymock;

import org.easymock.EasyMock;
import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ForecastProcessorUnitTest {
	private static final int MAX_TEMP = 90;

	@Rule
	public EasyMockRule rule = new EasyMockRule(this);

	@TestSubject
	private final ForecastProcessor forecastProcessor = new ForecastProcessor();

	@Mock
	private WeatherService mockWeatherService;

	@Before
	public void setUp() {
		forecastProcessor.setWeatherService(mockWeatherService);
	}

	@Test
	public void givenLocationName_whenWeatherServicePopulatesTemperatures_thenMaxTempReturned() throws ServiceUnavailableException {
		mockWeatherService.populateTemperature(EasyMock.anyObject(Location.class));
		EasyMock.expectLastCall()
				.andAnswer(() -> {
					Location passedLocation = (Location) EasyMock.getCurrentArguments()[0];
					passedLocation.setMaximumTemperature(new BigDecimal(MAX_TEMP));
					passedLocation.setMinimumTemperature(new BigDecimal(MAX_TEMP - 10));
					return null;
				});
		EasyMock.replay(mockWeatherService);
		BigDecimal maxTemperature = forecastProcessor.getMaximumTemperature("New York");
		EasyMock.verify(mockWeatherService);
		assertThat(maxTemperature, equalTo(new BigDecimal(MAX_TEMP)));
	}
}