package cn.tuyucheng.taketoday.facade;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CarEngineFacadeIntegrationTest {


	private InMemoryCustomTestAppender appender;


	@BeforeEach
	void setUp() {
		appender = new InMemoryCustomTestAppender();
	}

	@AfterEach
	void tearDown() {
		appender.stop();
	}


	@Test
	void whenStartEngine_thenAllNecessaryActionsPerformed() {
		CarEngineFacade carEngineFacade = new CarEngineFacade();
		carEngineFacade.startEngine();
		assertTrue(appender.logContains("Fuel injector ready to inject fuel."));
		assertTrue(appender.logContains("Getting air measurements.."));
		assertTrue(appender.logContains("Air provided!"));
		assertTrue(appender.logContains("Fuel injector ready to inject fuel."));
		assertTrue(appender.logContains("Fuel Pump is pumping fuel.."));
		assertTrue(appender.logContains("Fuel injected."));
		assertTrue(appender.logContains("Starting.."));
		assertTrue(appender.logContains("Setting temperature upper limit to 90"));
		assertTrue(appender.logContains("Cooling Controller ready!"));
		assertTrue(appender.logContains("Setting radiator speed to 10"));
		assertTrue(appender.logContains("Catalytic Converter switched on!"));
	}


	@Test
	void whenStopEngine_thenAllNecessaryActionsPerformed() {
		CarEngineFacade carEngineFacade = new CarEngineFacade();
		carEngineFacade.stopEngine();
		assertTrue(appender.logContains("Stopping Fuel injector.."));
		assertTrue(appender.logContains("Catalytic Converter switched off!"));
		assertTrue(appender.logContains("Scheduled cooling with maximum allowed temperature 50"));
		assertTrue(appender.logContains("Getting temperature from the sensor.."));
		assertTrue(appender.logContains("Radiator switched on!"));
		assertTrue(appender.logContains("Stopping Cooling Controller.."));
		assertTrue(appender.logContains("Radiator switched off!"));
		assertTrue(appender.logContains("Air controller switched off."));
	}

	private class InMemoryCustomTestAppender extends AppenderBase<ILoggingEvent> {

		private final List<ILoggingEvent> logs = new ArrayList<>();

		InMemoryCustomTestAppender() {
			((Logger) LoggerFactory.getLogger("root")).addAppender(this);
			start();
		}

		@Override
		protected void append(ILoggingEvent eventObject) {
			logs.add(eventObject);
		}

		boolean logContains(String message) {
			return logs.stream().anyMatch(event -> event.getFormattedMessage().equals(message));
		}
	}
}