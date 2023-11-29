package cn.tuyucheng.taketoday.spring.cloud.consul;

import cn.tuyucheng.taketoday.spring.cloud.consul.discovery.DiscoveryClientApplication;
import cn.tuyucheng.taketoday.spring.cloud.consul.health.ServiceDiscoveryApplication;
import cn.tuyucheng.taketoday.spring.cloud.consul.properties.DistributedPropertiesApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * This Live test requires:
 * * a Consul instance running on port 8500
 * * Consul configured with particular properties (e.g. 'my.prop')
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {DiscoveryClientApplication.class, ServiceDiscoveryApplication.class,
      DistributedPropertiesApplication.class})
class SpringContextLiveTest {

   @Test
   void whenSpringContextIsBootstrapped_thenNoExceptions() {
   }
}