package cn.tuyucheng.taketoday.startup;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringStartupConfig.class}, loader = AnnotationConfigContextLoader.class)
class SpringStartupIntegrationTest {

   @Autowired
   private ApplicationContext ctx;

   @Test
   void whenInstantiating_shouldThrowBCE() {
      assertThrows(BeanCreationException.class, () -> ctx.getBean(InvalidInitExampleBean.class));
   }

   @Test
   void whenPostConstruct_shouldLogEnv() {
      ctx.getBean(PostConstructExampleBean.class);
   }

   @Test
   void whenConstructorInjection_shouldLogEnv() {
      ctx.getBean(LogicInConstructorExampleBean.class);
   }

   @Test
   void whenInitializingBean_shouldLogEnv() {
      ctx.getBean(InitializingBeanExampleBean.class);
   }

   @Test
   void whenInitMethod_shouldLogEnv() {
      ctx.getBean(InitMethodExampleBean.class);
   }

   @Test
   void whenApplicationListener_shouldRunOnce() {
      Assertions.assertThat(StartupApplicationListenerExample.counter).isEqualTo(1);
   }
}