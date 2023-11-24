package cn.tuyucheng.taketoday.conditionalonthreading;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.springframework.boot.autoconfigure.condition.ConditionalOnThreading;
import org.springframework.boot.autoconfigure.thread.Threading;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

public class ConditionalOnThreadingUnitTest {

   ApplicationContextRunner applicationContextRunner = new ApplicationContextRunner()
         .withUserConfiguration(CurrentConfig.class);

   @Configuration
   static class CurrentConfig {

      @Bean
      @ConditionalOnThreading(Threading.PLATFORM)
      ThreadingType platformBean() {
         return ThreadingType.PLATFORM;
      }

      @Bean
      @ConditionalOnThreading(Threading.VIRTUAL)
      ThreadingType virtualBean() {
         return ThreadingType.VIRTUAL;
      }
   }

   enum ThreadingType {
      PLATFORM, VIRTUAL
   }

   @Test
   @EnabledForJreRange(max = JRE.JAVA_20)
   public void whenJava20AndVirtualThreadsEnabled_thenThreadingIsPlatform() {
      applicationContextRunner.withPropertyValues("spring.threads.virtual.enabled=true")
            .run(context -> assertThat(context.getBean(ThreadingType.class)).isEqualTo(ThreadingType.PLATFORM));
   }

   @Test
   @EnabledForJreRange(max = JRE.JAVA_20)
   public void whenJava20AndVirtualThreadsDisabled_thenThreadingIsPlatform() {
      applicationContextRunner.withPropertyValues("spring.threads.virtual.enabled=false")
            .run(context -> assertThat(context.getBean(ThreadingType.class)).isEqualTo(ThreadingType.PLATFORM));
   }

   @Test
   @EnabledForJreRange(min = JRE.JAVA_21)
   public void whenJava21AndVirtualThreadsEnabled_thenThreadingIsVirtual() {
      applicationContextRunner.withPropertyValues("spring.threads.virtual.enabled=true")
            .run(context -> assertThat(context.getBean(ThreadingType.class)).isEqualTo(ThreadingType.VIRTUAL));
   }

   @Test
   @EnabledForJreRange(min = JRE.JAVA_21)
   public void whenJava21AndVirtualThreadsDisabled_thenThreadingIsPlatform() {
      applicationContextRunner.withPropertyValues("spring.threads.virtual.enabled=false")
            .run(context -> assertThat(context.getBean(ThreadingType.class)).isEqualTo(ThreadingType.PLATFORM));
   }
}