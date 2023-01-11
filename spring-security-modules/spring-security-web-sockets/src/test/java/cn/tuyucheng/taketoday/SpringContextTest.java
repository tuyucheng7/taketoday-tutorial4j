package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.springsecuredsockets.config.AppConfig;
import cn.tuyucheng.taketoday.springsecuredsockets.config.DataStoreConfig;
import cn.tuyucheng.taketoday.springsecuredsockets.config.SecurityConfig;
import cn.tuyucheng.taketoday.springsecuredsockets.config.SocketBrokerConfig;
import cn.tuyucheng.taketoday.springsecuredsockets.config.SocketSecurityConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class, DataStoreConfig.class, SecurityConfig.class,
      SocketBrokerConfig.class, SocketSecurityConfig.class})
@WebAppConfiguration
public class SpringContextTest {

    @Test
    public void whenSpringContextIsBootstrapped_thenNoExceptions() {
    }
}
