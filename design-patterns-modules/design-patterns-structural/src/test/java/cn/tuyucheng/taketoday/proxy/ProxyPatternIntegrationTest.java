package cn.tuyucheng.taketoday.proxy;

import org.apache.log4j.spi.LoggingEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static cn.tuyucheng.taketoday.util.LoggerUtil.LOG;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class ProxyPatternIntegrationTest {
	static TestAppenderDP appender;

	@BeforeEach
	void setUp() {
		appender = new TestAppenderDP();
		LOG.addAppender(appender);
	}

	@Test
	void givenExpensiveObjectProxy_WhenObjectInitialized_thenInitializedOnlyOnce() {
		ExpensiveObject object = new ExpensiveObjectProxy();
		object.process();
		object.process();

		final List<LoggingEvent> log = appender.getLog();

		assertThat((String) log.get(0).getMessage(), is("Loading initial configuration.."));
		assertThat((String) log.get(1).getMessage(), is("processing complete."));
		assertThat((String) log.get(2).getMessage(), is("processing complete."));
	}

	@AfterEach
	void tearDown() {
		LOG.removeAppender(appender);
	}
}