package cn.tuyucheng.taketoday.boot.configurationproperties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = MailServer.class)
@TestPropertySource(properties = {"validate.mail_config.address=new_user@test"})
class OverridingConfigurationPropertiesUnitTest {

	@Autowired
	private MailServer mailServer;

	@Test
	void givenUsingPropertiesAttribute_whenAssiginingNewValueToProperty_thenSpringUsesNewValue() {
		assertEquals("new_user@test", mailServer.getMailConfig().getAddress());

		Map<String, String> expectedMap = new HashMap<>();
		expectedMap.put("first", "prop1");
		expectedMap.put("second", "prop2");

		assertEquals(expectedMap, mailServer.getPropertiesMap());
	}
}