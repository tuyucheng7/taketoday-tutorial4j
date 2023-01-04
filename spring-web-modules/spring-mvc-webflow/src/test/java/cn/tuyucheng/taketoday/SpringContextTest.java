package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.spring.WebFlowConfig;
import cn.tuyucheng.taketoday.spring.WebMvcConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebFlowConfig.class, WebMvcConfig.class})
@WebAppConfiguration
public class SpringContextTest {

    @Test
    public void whenSpringContextIsBootstrapped_thenNoExceptions() {
    }
}