package cn.tuyucheng.taketoday.yaml;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MyApplication.class)
@TestPropertySource(properties = {"spring.profiles.active = test"})
class YAMLIntegrationTest {

	@Autowired
	private YAMLConfig config;

	@Test
	void whenProfileTest_thenNameTesting() {
		assertTrue("testing".equalsIgnoreCase(config.getEnvironment()));
		assertTrue("test-YAML".equalsIgnoreCase(config.getName()));
		assertTrue("myurl".equalsIgnoreCase(config.getComponent().getIdm().getUrl()));
		assertFalse(config.isEnabled());
	}
}