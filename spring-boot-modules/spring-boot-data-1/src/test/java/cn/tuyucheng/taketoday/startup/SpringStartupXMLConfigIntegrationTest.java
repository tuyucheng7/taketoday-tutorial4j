package cn.tuyucheng.taketoday.startup;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:startupConfig.xml")
class SpringStartupXMLConfigIntegrationTest {

   @Autowired
   private ApplicationContext ctx;

   @Test
   void whenInitMethod_shouldLogEnv() {
      ctx.getBean(InitMethodExampleBean.class);
   }

   @Test
   void whenAllStrategies_shouldLogOrder() {
      ctx.getBean(AllStrategiesExampleBean.class);
   }
}