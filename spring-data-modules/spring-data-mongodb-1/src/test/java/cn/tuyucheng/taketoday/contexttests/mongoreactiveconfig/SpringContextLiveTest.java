package cn.tuyucheng.taketoday.contexttests.mongoreactiveconfig;

import cn.tuyucheng.taketoday.config.MongoReactiveConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * This Live test requires:
 * * mongodb instance running on the environment
 * <p>
 * (e.g. `docker run -d -p 27017:27017 --name bael-mongo mongo`)
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MongoReactiveConfig.class)
class SpringContextLiveTest {

   @Test
   void whenSpringContextIsBootstrapped_thenNoExceptions() {
   }
}
