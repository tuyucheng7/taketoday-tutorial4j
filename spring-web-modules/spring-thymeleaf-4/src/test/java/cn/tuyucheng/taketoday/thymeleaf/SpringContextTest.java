package cn.tuyucheng.taketoday.thymeleaf;

import cn.tuyucheng.taketoday.thymeleaf.config.WebApp;
import cn.tuyucheng.taketoday.thymeleaf.config.WebMVCConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebApp.class, WebMVCConfig.class})
public class SpringContextTest {

    @Test
    public void whenSpringContextIsBootstrapped_thenNoExceptions() {
    }
}