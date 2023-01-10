package cn.tuyucheng.taketoday.spring.cloud.consul;

import cn.tuyucheng.taketoday.spring.cloud.consul.discovery.DiscoveryClientApplication;
import cn.tuyucheng.taketoday.spring.cloud.consul.health.ServiceDiscoveryApplication;
import cn.tuyucheng.taketoday.spring.cloud.consul.properties.DistributedPropertiesApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * This Live test requires:
 * * a Consul instance running on port 8500
 * * Consul configured with particular properties (e.g. 'my.prop')
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DiscoveryClientApplication.class, ServiceDiscoveryApplication.class,
	DistributedPropertiesApplication.class})
public class SpringContextLiveTest {

	@Test
	public void whenSpringContextIsBootstrapped_thenNoExceptions() {
	}
}