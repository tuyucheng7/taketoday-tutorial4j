package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.spring.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Note: In the IDE, remember to generate query type classes before running the Integration Test (e.g. in Eclipse right-click on the project > Run As > Maven generate sources)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class SpringContextTest {

    @Test
    public void whenSpringContextIsBootstrapped_thenNoExceptions() {
    }
}