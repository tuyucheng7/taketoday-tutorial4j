package cn.tuyucheng.taketoday.spring.cloud.config.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * The app needs the server running on port 8888. Can be started with docker
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ConfigClient.class)
@WebAppConfiguration
class SpringContextLiveTest {
    @Test
    void contextLoads() {
    }
}