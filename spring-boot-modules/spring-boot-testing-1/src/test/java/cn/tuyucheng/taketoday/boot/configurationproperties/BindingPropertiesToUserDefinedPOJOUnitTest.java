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
@EnableConfigurationProperties(value = ServerConfig.class)
@TestPropertySource("classpath:server-config-test.properties")
class BindingPropertiesToUserDefinedPOJOUnitTest {

	@Autowired
	private ServerConfig serverConfig;

	@Test
	void givenUserDefinedPOJO_whenBindingPropertiesFile_thenAllFieldsAreSet() {
		assertEquals("192.168.0.1", serverConfig.getAddress().getIp());

		Map<String, String> expectedResourcesPath = new HashMap<>();
		expectedResourcesPath.put("imgs", "/root/imgs");

		assertEquals(expectedResourcesPath, serverConfig.getResourcesPath());
	}
}