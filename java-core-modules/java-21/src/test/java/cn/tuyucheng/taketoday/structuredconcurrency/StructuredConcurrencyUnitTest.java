package cn.tuyucheng.taketoday.structuredconcurrency;

import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

import static java.util.concurrent.StructuredTaskScope.Subtask;
import static org.assertj.core.api.Assertions.assertThat;

public class StructuredConcurrencyUnitTest {

   @Test
   public void givenUserId_whenParallelWithStructuredConcurrency_thenReturnMessageCorrect() throws Exception {
      var message = parallelSearch("42");

      assertThat(message).isEqualTo("The real name of '42' is 'Thomas Anderson' and its power is Over 9000");
   }

   private String parallelSearch(String userId) throws InterruptedException, ExecutionException {
      try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
         // JDK 21: changed `fork` to return `Subtask` instead of `Future`
         Subtask<String> userName = scope.fork(() -> this.findName(userId));
         Subtask<String> answer = scope.fork(() -> this.findPower(userId));

         scope.join();
         scope.throwIfFailed();

         // `Subtask::get` behaves like `Future::resultNow`
         return STR."The real name of '\{userId}' is '\{userName.get()}' and its power is \{answer.get()}";
      }
   }

   private String findName(String userId) {
      return "Thomas Anderson";
   }

   private String findPower(String userId) {
      try {
         Thread.sleep(3000);
      } catch (Exception ex) {
      }
      return "Over 9000";
   }
}