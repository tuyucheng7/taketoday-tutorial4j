package cn.tuyucheng.taketoday.newfeatures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringUnitTest {

	@Test
	void givenString_thenRevertValue() {
		String text = "Tuyucheng";
		String transformed = text.transform(value ->
			new StringBuilder(value).reverse().toString());
		assertEquals("gnehcuyuT", transformed);
	}
}