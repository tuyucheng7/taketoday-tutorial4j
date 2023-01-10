package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.task.JobConfiguration;
import cn.tuyucheng.taketoday.task.TaskDemo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootApplication
@ContextConfiguration(classes = {JobConfiguration.class, TaskDemo.class}, initializers = {ConfigDataApplicationContextInitializer.class})
public class SpringContextTest {

    @Test
    public void whenSpringContextIsBootstrapped_thenNoExceptions() {
    }
}