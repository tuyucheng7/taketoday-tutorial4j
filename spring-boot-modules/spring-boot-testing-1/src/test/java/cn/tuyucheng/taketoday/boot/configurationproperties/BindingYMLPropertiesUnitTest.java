package cn.tuyucheng.taketoday.boot.configurationproperties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = ServerConfig.class)
@ActiveProfiles("test")
class BindingYMLPropertiesUnitTest {

	@Autowired
	private ServerConfig serverConfig;

	@Test
	void whenBindingYMLConfigFile_thenAllFieldsAreSet() {
		assertEquals("192.168.0.4", serverConfig.getAddress().getIp());

		Map<String, String> expectedResourcesPath = new HashMap<>();
		expectedResourcesPath.put("imgs", "/etc/test/imgs");

		assertEquals(expectedResourcesPath, serverConfig.getResourcesPath());
	}
}