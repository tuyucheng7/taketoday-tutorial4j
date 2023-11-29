package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.kclkpl.KinesisKPLApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Manual Test - this test needs correct AWS Access Key and Secret to build the Amazon Kinesis and complete successfully
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = KinesisKPLApplication.class)
class KinesisApplicationManualTest {
   @Test
   void whenSpringContextIsBootstrapped_thenNoExceptions() {
   }
}