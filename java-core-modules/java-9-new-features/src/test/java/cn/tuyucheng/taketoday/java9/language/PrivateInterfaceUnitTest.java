package cn.tuyucheng.taketoday.java9.language;

import org.junit.jupiter.api.Test;

class PrivateInterfaceUnitTest {

	@Test
	void test() {
		PrivateInterface piClass = new PrivateInterface() {
		};
		piClass.check();
	}
}