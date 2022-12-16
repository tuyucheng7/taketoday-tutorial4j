package cn.tuyucheng.taketoday.facade;

import cn.tuyucheng.taketoday.util.InMemoryCustomTestAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}