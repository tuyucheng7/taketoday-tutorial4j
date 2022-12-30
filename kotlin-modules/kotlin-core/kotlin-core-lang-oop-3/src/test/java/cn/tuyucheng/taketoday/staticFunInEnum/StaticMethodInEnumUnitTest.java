package cn.tuyucheng.taketoday.staticFunInEnum;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static cn.tuyucheng.taketoday.staticFunInEnum.MagicNumber.FIVE;
import static cn.tuyucheng.taketoday.staticFunInEnum.MagicNumber.FOUR;
import static cn.tuyucheng.taketoday.staticFunInEnum.MagicNumber.SIX;
import static cn.tuyucheng.taketoday.staticFunInEnum.MagicNumber.THREE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StaticMethodInEnumUnitTest {
	@Test
	void givenKotlinEnum_WhenCallStaticMethod_ShouldGetExpectedResult() {
		// the greaterThan() function with @JvmStatic
		assertEquals(Arrays.asList(THREE, FOUR, FIVE, SIX), MagicNumber.greaterThan(2));

		// calling it through the Companion object
		assertEquals(Arrays.asList(THREE, FOUR, FIVE, SIX), MagicNumber.Companion.greaterThan(2));

		// the pickOneRandomly() function without @JvmStatic
		assertNotNull(MagicNumber.Companion.pickOneRandomly());
	}
}