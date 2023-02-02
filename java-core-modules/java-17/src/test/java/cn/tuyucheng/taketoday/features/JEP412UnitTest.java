package cn.tuyucheng.taketoday.features;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JEP412UnitTest {

	@Test
	@Disabled("Need C Language Environment")
	void getPrintNameFormat_whenPassingAName_shouldReceiveItFormatted() {
		var jep412 = new JEP412();

		var formattedName = jep412.getPrintNameFormat("John");

		assertEquals("Your name is John", formattedName);
	}
}