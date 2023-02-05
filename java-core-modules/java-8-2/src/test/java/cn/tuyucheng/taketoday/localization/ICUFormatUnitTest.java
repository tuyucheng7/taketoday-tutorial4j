package cn.tuyucheng.taketoday.localization;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ICUFormatUnitTest {

	@Test
	void givenInUK_whenAliceSendsNothing_thenCorrectMessage() {
		assertEquals("Alice has sent you no messages.", ICUFormat.getLabel(Locale.UK, new Object[]{"Alice", "female", 0}));
	}

	@Test
	void givenInUK_whenAliceSendsOneMessage_thenCorrectMessage() {
		assertEquals("Alice has sent you a message.", ICUFormat.getLabel(Locale.UK, new Object[]{"Alice", "female", 1}));
	}

	@Test
	void givenInUK_whenBobSendsSixMessages_thenCorrectMessage() {
		assertEquals("Bob has sent you 6 messages.", ICUFormat.getLabel(Locale.UK, new Object[]{"Bob", "male", 6}));
	}

	@Test
	void givenInItaly_whenAliceSendsNothing_thenCorrectMessage() {
		assertEquals("Alice non ti ha inviato nessun messaggio.", ICUFormat.getLabel(Locale.ITALY, new Object[]{"Alice", "female", 0}));
	}

	@Test
	void givenInItaly_whenAliceSendsOneMessage_thenCorrectMessage() {
		assertEquals("Alice ti ha inviato un messaggio.", ICUFormat.getLabel(Locale.ITALY, new Object[]{"Alice", "female", 1}));
	}

	@Test
	void givenInItaly_whenBobSendsSixMessages_thenCorrectMessage() {
		assertEquals("Bob ti ha inviato 6 messaggi.", ICUFormat.getLabel(Locale.ITALY, new Object[]{"Bob", "male", 6}));
	}

	@Test
	void givenInFrance_whenAliceSendsNothing_thenCorrectMessage() {
		assertEquals("Alice ne vous a envoyé aucun message.", ICUFormat.getLabel(Locale.FRANCE, new Object[]{"Alice", "female", 0}));
	}

	@Test
	void givenInFrance_whenAliceSendsOneMessage_thenCorrectMessage() {
		assertEquals("Alice vous a envoyé un message.", ICUFormat.getLabel(Locale.FRANCE, new Object[]{"Alice", "female", 1}));
	}

	@Test
	void givenInFrance_whenBobSendsSixMessages_thenCorrectMessage() {
		assertEquals("Bob vous a envoyé 6 messages.", ICUFormat.getLabel(Locale.FRANCE, new Object[]{"Bob", "male", 6}));
	}


	@Test
	void givenInPoland_whenAliceSendsNothing_thenCorrectMessage() {
		assertEquals("Alice nie wysłała ci żadnej wiadomości.", ICUFormat.getLabel(Locale.forLanguageTag("pl-PL"), new Object[]{"Alice", "female", 0}));
	}

	@Test
	void givenInPoland_whenAliceSendsOneMessage_thenCorrectMessage() {
		assertEquals("Alice wysłała ci wiadomość.", ICUFormat.getLabel(Locale.forLanguageTag("pl-PL"), new Object[]{"Alice", "female", 1}));
	}

	@Test
	void givenInPoland_whenBobSendsSixMessages_thenCorrectMessage() {
		assertEquals("Bob wysłał ci 6 wiadomości.", ICUFormat.getLabel(Locale.forLanguageTag("pl-PL"), new Object[]{"Bob", "male", 6}));
	}
}