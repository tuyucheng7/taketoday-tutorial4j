package cn.tuyucheng.taketoday.springbootadminserver;

import cn.tuyucheng.taketoday.springbootadminserver.configs.HazelcastConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {HazelcastConfig.class}, webEnvironment = NONE)
class HazelcastConfigIntegrationTest {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void whenApplicationContextStarts_HazelcastConfigBeanExists() {
		assertNotEquals(null, applicationContext.getBean("hazelcast"));
	}
}