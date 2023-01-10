package cn.tuyucheng.taketoday;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import cn.tuyucheng.taketoday.spring.cloud.kubernetes.client.Application;

@SpringBootTest(classes = Application.class)
public class SpringContextTest {

    @Test
    public void contextLoads() {
    }
}
