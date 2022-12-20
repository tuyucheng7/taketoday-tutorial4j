package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.autoconfiguration.MySQLAutoconfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MySQLAutoconfiguration.class)
@WebAppConfiguration
class SpringContextLiveTest {

	@Test
	void whenSpringContextIsBootstrapped_thenNoExceptions() {
	}
}