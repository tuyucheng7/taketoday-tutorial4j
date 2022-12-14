package cn.tuyucheng.taketoday.singleton;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SingletonUnitTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(SingletonUnitTest.class);

	@Test
	void givenClassSingleton_whenGetInstanceTwice_thenShouldEquals() {
		ClassSingleton classSingleton1 = ClassSingleton.getInstance();

		LOGGER.info("classSingleton initial info: {}", classSingleton1.getInfo());

		ClassSingleton classSingleton2 = ClassSingleton.getInstance();
		classSingleton2.setInfo("New class info");

		assertEquals("New class info", classSingleton1.getInfo());
		assertEquals("New class info", classSingleton2.getInfo());
	}

	@Test
	void givenEnumSingleton_whenGetInstanceTwice_thenShouldEquals() {
		EnumSingleton enumSingleton1 = EnumSingleton.INSTANCE.getInstance();

		LOGGER.info("enumSingleton initial info: {}", enumSingleton1.getInfo());

		EnumSingleton enumSingleton2 = EnumSingleton.INSTANCE.getInstance();
		enumSingleton2.setInfo("New enum info");

		assertEquals("New enum info", enumSingleton1.getInfo());
		assertEquals("New enum info", enumSingleton2.getInfo());
	}
}