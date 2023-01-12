package cn.tuyucheng.taketoday.lombok;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FarewellServiceIntegrationTest {

	private final static String TRANSLATED = "TRANSLATED";

	@Test
	public void sayByeWithTranslatedMessage() {
		Translator translator = mock(Translator.class);
		when(translator.translate("bye")).thenReturn(TRANSLATED);
		FarewellService farewellService = new FarewellService(translator);
		assertEquals(TRANSLATED, farewellService.farewell());
	}
}