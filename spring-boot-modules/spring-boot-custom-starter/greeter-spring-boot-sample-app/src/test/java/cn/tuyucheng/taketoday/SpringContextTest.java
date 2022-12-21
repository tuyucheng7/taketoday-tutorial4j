package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.greeter.sample.GreeterSampleApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GreeterSampleApplication.class)
class SpringContextTest {

    @Test
    void whenSpringContextIsBootstrapped_thenNoExceptions() {
    }
}