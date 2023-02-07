package cn.tuyucheng.taketoday.spring.cloud.archaius.zookeeperconfig;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * This Live tTest requires:
 * <br/>
 * A Zookeeper instance running locally on port 2181 (e.g. using  `docker run --name tuyucheng-zookeeper -p 2181:2181 --restart always zookeeper`)
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ZookeeperConfigApplication.class)
class SpringContextLiveTest {

	@Test
	void whenSpringContextIsBootstrapped_thenNoExceptions() {
	}
}