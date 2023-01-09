package cn.tuyucheng.taketoday.test.dependencyinjection;

import cn.tuyucheng.taketoday.dependencyinjection.factories.TimeLoggerFactory;
import cn.tuyucheng.taketoday.dependencyinjection.loggers.TimeLogger;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeLoggerFactoryUnitTest {
	@Test
	public void givenTimeLoggerFactory_whenCalledgetTimeLogger_thenOneAssertion() {
		TimeLoggerFactory timeLoggerFactory = new TimeLoggerFactory();
		assertThat(timeLoggerFactory.getTimeLogger()).isInstanceOf(TimeLogger.class);
	}
}
