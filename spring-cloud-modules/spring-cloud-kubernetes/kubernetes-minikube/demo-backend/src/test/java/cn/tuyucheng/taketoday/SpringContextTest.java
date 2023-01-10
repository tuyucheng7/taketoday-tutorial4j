package cn.tuyucheng.taketoday;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import cn.tuyucheng.taketoday.spring.cloud.kubernetes.backend.KubernetesBackendApplication;

@SpringBootTest(classes = KubernetesBackendApplication.class)
public class SpringContextTest {

    @Test
    public void contextLoads() {
    }

}
