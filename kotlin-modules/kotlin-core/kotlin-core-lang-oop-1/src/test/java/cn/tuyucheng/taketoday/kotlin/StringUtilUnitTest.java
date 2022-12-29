package cn.tuyucheng.taketoday.kotlin;

import kotlin.text.StringsKt;
import org.junit.jupiter.api.Test;

import static cn.tuyucheng.taketoday.kotlin.Strings.escapeForXml;
import static org.junit.jupiter.api.Assertions.assertEquals;


class StringUtilUnitTest {

	@Test
	void shouldEscapeXmlTagsInString() {
		String xml = "<a>hi</a>";

		String escapedXml = escapeForXml(xml);

		assertEquals("&lt;a&gt;hi&lt;/a&gt;", escapedXml);
	}

	@Test
	void callingBuiltInKotlinExtensionMethod() {
		String name = "john";

		String capitalizedName = StringsKt.capitalize(name);

		assertEquals("John", capitalizedName);
	}
}