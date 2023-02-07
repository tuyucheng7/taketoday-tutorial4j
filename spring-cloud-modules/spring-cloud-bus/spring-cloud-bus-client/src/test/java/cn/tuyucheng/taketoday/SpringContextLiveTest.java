package cn.tuyucheng.taketoday;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * This LiveTest requires:
 * 1- The 'spring-cloud-bus/spring-cloud-config-server' service running, with valid Spring Cloud Config properties, for instance:
 * <br/>
 * 1.1- `spring.cloud.config.server.git.uri` pointing to a valid URI, which has `user.role` and `user.password` properties configured in a properties file. For example, to achieve this in a dev environment:
 * <br/>
 * 1.1.1- `cd my/custom/path`
 * <br/>
 * 1.1.2- `git init .`
 * <br/>
 * 1.1.3- `echo user.role=Programmer >> application.properties`
 * <br/>
 * 1.1.4- `echo user.password=d3v3L >> application.properties`
 * <br/>
 * 1.1.5- `git add .`
 * <br/>
 * 1.1.6- `git commit -m 'initial commit'`
 * <br/>
 * 1.1.7- 'spring-cloud-bus/spring-cloud-config-server' with property `spring.cloud.config.server.git.uri=my/custom/path`
 * <br/>
 * <p>
 * Note: This is enough to run the ContextTest successfully, but to get the full functionality shown in the related article, we'll need also a RabbitMQ instance running (e.g. `docker run -d --hostname my-rabbit --name some-rabbit -p 15672:15672 -p 5672:5672 rabbitmq:3-management`)
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringCloudConfigClientApplication.class)
class SpringContextLiveTest {

    @Test
    void contextLoads() {
    }
}