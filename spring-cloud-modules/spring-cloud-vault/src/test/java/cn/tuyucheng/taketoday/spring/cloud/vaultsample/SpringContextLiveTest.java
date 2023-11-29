package cn.tuyucheng.taketoday.spring.cloud.vaultsample;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * This live test requires:
 * vault server up and running on the environment
 *
 * <br>
 * For more info on setting up the vault server:
 * https://www.baeldung.com/vault
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = VaultSampleApplication.class)
class SpringContextLiveTest {

   @Test
   void whenSpringContextIsBootstrapped_thenNoExceptions() {
   }
}