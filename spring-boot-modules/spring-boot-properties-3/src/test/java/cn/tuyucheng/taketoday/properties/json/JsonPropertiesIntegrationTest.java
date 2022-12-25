package cn.tuyucheng.taketoday.properties.json;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ConfigPropertiesDemoApplication.class, initializers = JsonPropertyContextInitializer.class)
class JsonPropertiesIntegrationTest {

	@Autowired
	private JsonProperties jsonProperties;

	@Autowired
	private CustomJsonProperties customJsonProperties;

	@Test
	void whenPropertiesLoadedViaJsonPropertySource_thenLoadFlatValues() {
		assertEquals("mailer@mail.com", jsonProperties.getHost());
		assertEquals(9090, jsonProperties.getPort());
		assertTrue(jsonProperties.isResend());
	}

	@Test
	void whenPropertiesLoadedViaJsonPropertySource_thenLoadListValues() {
		assertThat(jsonProperties.getTopics(), Matchers.is(Arrays.asList("spring", "boot")));
	}

	@Test
	void whenPropertiesLoadedViaJsonPropertySource_thenNestedLoadedAsMap() {
		assertEquals("sender", jsonProperties.getSender().get("name"));
		assertEquals("street", jsonProperties.getSender().get("address"));
	}

	@Test
	void whenLoadedIntoEnvironment_thenFlatValuesPopulated() {
		assertEquals("mailer@mail.com", customJsonProperties.getHost());
		assertEquals(9090, customJsonProperties.getPort());
		assertTrue(customJsonProperties.isResend());
	}

	@Test
	void whenLoadedIntoEnvironment_thenValuesLoadedIntoClassObject() {
		assertNotNull(customJsonProperties.getSender());
		assertEquals("sender", customJsonProperties.getSender().getName());
		assertEquals("street", customJsonProperties.getSender().getAddress());
	}
}