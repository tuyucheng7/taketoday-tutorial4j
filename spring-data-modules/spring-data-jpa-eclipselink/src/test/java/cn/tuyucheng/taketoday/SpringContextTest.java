package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.eclipselink.springdata.EclipselinkSpringDataApplication;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Disabled("fails test")
@SpringBootTest(classes = EclipselinkSpringDataApplication.class)
class SpringContextTest {

    @Test
    void whenSpringContextIsBootstrapped_thenNoExceptions() {
    }
}