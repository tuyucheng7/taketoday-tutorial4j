package cn.tuyucheng.taketoday.yaml;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MyApplication.class)
@TestPropertySource(properties = {"spring.profiles.active = dev"})
class YAMLDevIntegrationTest {

	@Autowired
	private YAMLConfig config;

	@Test
	void whenProfileTest_thenNameTesting() {
		assertTrue("development".equalsIgnoreCase(config.getEnvironment()));
		assertTrue("dev-YAML".equalsIgnoreCase(config.getName()));
		assertTrue(config.isEnabled());
	}
}