package cn.tuyucheng.taketoday.static

import org.junit.jupiter.api.Test

class ConsoleUtilsUnitTest {
	@Test
	fun givenAStaticMethod_whenCalled_thenNoErrorIsThrown() {
		ConsoleUtils.debug("test message")
	}
}