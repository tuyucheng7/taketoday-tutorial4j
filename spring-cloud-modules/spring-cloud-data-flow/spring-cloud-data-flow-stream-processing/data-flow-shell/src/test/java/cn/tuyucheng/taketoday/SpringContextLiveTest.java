package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.spring.cloud.DataFlowShellApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * This live test requires:
 * complete data-flow server and shell setup running
 *
 * <br>
 * For more info:
 * https://www.baeldung.com/spring-cloud-data-flow-stream-processing
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DataFlowShellApplication.class)
class SpringContextLiveTest {

   @Test
   void contextLoads() {
   }
}