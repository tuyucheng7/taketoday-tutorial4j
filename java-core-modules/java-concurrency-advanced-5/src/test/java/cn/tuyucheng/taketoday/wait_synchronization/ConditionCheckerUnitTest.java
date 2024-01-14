package cn.tuyucheng.taketoday.wait_synchronization;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ConditionCheckerUnitTest {

   @Test
   public void givenBothMethodsAreSynchronized_whenBothMethodsAreCalled_thenNoExceptionsOrDeadlocks() {
      ConditionChecker conditionChecker = new ConditionChecker();

      ExecutorService executorService = Executors.newFixedThreadPool(2);

      Assertions.assertThatCode(() -> {
         executorService.submit(conditionChecker::ensureCondition);
         executorService.submit(conditionChecker::complete);
      }).doesNotThrowAnyException();
   }
}