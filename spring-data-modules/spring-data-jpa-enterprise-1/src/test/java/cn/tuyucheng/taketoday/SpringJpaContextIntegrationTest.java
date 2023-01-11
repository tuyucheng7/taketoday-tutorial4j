package cn.tuyucheng.taketoday;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import cn.tuyucheng.taketoday.boot.Application;
import cn.tuyucheng.taketoday.boot.config.PersistenceConfiguration;

@ExtendWith(SpringExtension.class)
@DataJpaTest(excludeAutoConfiguration = {
        PersistenceConfiguration.class})
@ContextConfiguration(classes = Application.class)
class SpringJpaContextIntegrationTest {

    @Test
    void whenSpringContextIsBootstrapped_thenNoExceptions() {
    }
}