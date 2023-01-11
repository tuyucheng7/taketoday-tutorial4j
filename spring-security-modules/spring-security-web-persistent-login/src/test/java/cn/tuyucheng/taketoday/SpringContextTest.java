package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.spring.MvcConfig;
import cn.tuyucheng.taketoday.spring.PersistenceConfig;
import cn.tuyucheng.taketoday.spring.SecurityConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MvcConfig.class, PersistenceConfig.class, SecurityConfig.class})
@WebAppConfiguration
public class SpringContextTest {

    @Test
    public void whenSpringContextIsBootstrapped_thenNoExceptions() {
    }
}
