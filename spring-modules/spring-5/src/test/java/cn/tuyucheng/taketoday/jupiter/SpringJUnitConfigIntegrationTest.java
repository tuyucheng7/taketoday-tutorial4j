package cn.tuyucheng.taketoday.jupiter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @SpringJUnitConfig(SpringJUnitConfigTest.Config.class) is equivalent to:
 * @ExtendWith(SpringExtension.class)
 * @ContextConfiguration(classes = SpringJUnitConfigTest.Config.class )
 */
@SpringJUnitConfig(SpringJUnitConfigIntegrationTest.Config.class)
class SpringJUnitConfigIntegrationTest {

	@Configuration
	static class Config {
	}

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void givenAppContext_WhenInjected_ThenItShouldNotBeNull() {
		assertNotNull(applicationContext);
	}
}