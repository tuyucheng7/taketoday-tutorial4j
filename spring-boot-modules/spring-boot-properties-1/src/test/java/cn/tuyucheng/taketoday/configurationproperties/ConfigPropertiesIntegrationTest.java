package cn.tuyucheng.taketoday.configurationproperties;

import cn.tuyucheng.taketoday.properties.AdditionalProperties;
import cn.tuyucheng.taketoday.properties.ConfigPropertiesDemoApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {ConfigPropertiesDemoApplication.class})
@TestPropertySource(locations = {"classpath:configprops-test.properties"})
class ConfigPropertiesIntegrationTest {

	@Autowired
	private ConfigProperties configProperties;

	@Autowired
	private AdditionalProperties additionalProperties;

	@Test
	void whenSimplePropertyQueriedthenReturnsProperty() throws Exception {
		assertNotNull(configProperties.getFrom(), "From address is read as null!");
	}

	@Test
	void whenListPropertyQueriedthenReturnsProperty() throws Exception {
		assertEquals(2, configProperties.getDefaultRecipients().size(), "Couldn't bind list property!");
		assertEquals(2, configProperties.getDefaultRecipients().size(), "Incorrectly bound list property. Expected 2 entries!");
	}

	@Test
	void whenMapPropertyQueriedthenReturnsProperty() throws Exception {
		assertNotNull(configProperties.getAdditionalHeaders(), "Couldn't bind map property!");
		assertEquals(3, configProperties.getAdditionalHeaders().size(), "Incorrectly bound map property. Expected 3 Entries!");
	}

	@Test
	void whenObjectPropertyQueriedthenReturnsProperty() throws Exception {
		assertNotNull(configProperties.getCredentials(), "Couldn't bind map property!");
		assertEquals("SHA1", configProperties.getCredentials().getAuthMethod(), "Incorrectly bound object property!");
		assertEquals("john", configProperties.getCredentials().getUsername(), "Incorrectly bound object property!");
		assertEquals("password", configProperties.getCredentials().getPassword(), "Incorrectly bound object property!");
	}

	@Test
	void whenAdditionalPropertyQueriedthenReturnsProperty() {
		assertEquals("km", additionalProperties.getUnit());
		assertEquals(100, additionalProperties.getMax());
	}
}