package cn.tuyucheng.taketoday.jar;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class MySampleGUIAppnUnitTest {
	@Test
	void testMain() throws IOException {
		System.setProperty("java.awt.headless", "true");
		String[] args = null;
		System.exit(0);
		MySampleGUIAppn.main(args);
	}
}