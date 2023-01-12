package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.si.security.MessageConsumer;
import cn.tuyucheng.taketoday.si.security.SecuredDirectChannel;
import cn.tuyucheng.taketoday.si.security.SecurityConfig;
import cn.tuyucheng.taketoday.si.security.SecurityPubSubChannel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SecurityConfig.class, SecuredDirectChannel.class, SecurityPubSubChannel.class,
	MessageConsumer.class})
public class SpringContextTest {

	@Test
	public void whenSpringContextIsBootstrapped_thenNoExceptions() {
	}
}
