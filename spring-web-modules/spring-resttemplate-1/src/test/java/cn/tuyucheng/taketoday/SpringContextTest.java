package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.resttemplate.RestTemplateConfigurationApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = RestTemplateConfigurationApplication.class)
class SpringContextTest {

    @Test
    void whenSpringContextIsBootstrapped_thenNoExceptions() {
    }
}