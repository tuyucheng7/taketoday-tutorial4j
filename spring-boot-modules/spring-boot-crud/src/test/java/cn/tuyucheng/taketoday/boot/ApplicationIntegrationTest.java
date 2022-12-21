package cn.tuyucheng.taketoday.boot;

import cn.tuyucheng.taketoday.session.exception.Application;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@TestPropertySource("classpath:exception.properties")
public class ApplicationIntegrationTest {

    @Test
    void contextLoads() {
    }
}