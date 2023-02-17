package cn.tuyucheng.taketoday.argsVsvarargs;

import org.junit.jupiter.api.Test;

import static cn.tuyucheng.taketoday.argsVsvarargs.StringArrayAndVarargs.capitalizeNames;
import static cn.tuyucheng.taketoday.argsVsvarargs.StringArrayAndVarargs.firstLetterOfWords;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StringArrayAndVarargsUnitTest {

	@Test
	void whenCheckingArgumentClassName_thenNameShouldBeStringArray() {
		String[] names = {"john", "ade", "kofi", "imo"};
		assertNotNull(names);
		assertEquals("java.lang.String[]", names.getClass().getTypeName());
		capitalizeNames(names);
	}

	@Test
	void whenCheckingReturnedObjectClass_thenClassShouldBeStringArray() {
		assertEquals(String[].class, firstLetterOfWords("football", "basketball", "volleyball").getClass());
		assertEquals(3, firstLetterOfWords("football", "basketball", "volleyball").length);
	}
}