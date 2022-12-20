package cn.tuyucheng.taketoday.autoconfiguration;

import cn.tuyucheng.taketoday.autoconfiguration.example.AutoconfigurationApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AutoconfigurationApplication.class)
@EnableJpaRepositories(basePackages = {"cn.tuyucheng.taketoday.autoconfiguration.example"})
class SpringContextLiveTest {

	@Test
	void whenSpringContextIsBootstrapped_thenNoExceptions() {
	}
}