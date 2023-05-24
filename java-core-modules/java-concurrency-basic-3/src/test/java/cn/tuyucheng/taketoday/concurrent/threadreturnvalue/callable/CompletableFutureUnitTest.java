package cn.tuyucheng.taketoday.concurrent.threadreturnvalue.callable;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import static cn.tuyucheng.taketoday.concurrent.threadreturnvalue.task.FactorialCalculator.factorial;
import static java.util.concurrent.CompletableFuture.allOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompletableFutureUnitTest {

   @Test
   void givenCompletableFuture_whenSupplyAsyncFactorial_thenResultOk() throws ExecutionException, InterruptedException {
      CompletableFuture<BigInteger> completableFuture = CompletableFuture.supplyAsync(() -> factorial(BigInteger.valueOf(10)));
      assertEquals(BigInteger.valueOf(3628800), completableFuture.get());
   }

   @Test
   void givenCompletableFuture_whenComposeTasks_thenResultOk() throws ExecutionException, InterruptedException {
      CompletableFuture<BigInteger> completableFuture = CompletableFuture.supplyAsync(() -> factorial(BigInteger.valueOf(3)))
            .thenCompose(inputFromFirstTask -> CompletableFuture.supplyAsync(() -> factorial(inputFromFirstTask)));
      assertEquals(BigInteger.valueOf(720), completableFuture.get());
   }

   @Test
   void givenCompletableFuture_whenAllOfTasks_thenResultOk() {
      CompletableFuture<BigInteger> asyncTask1 = CompletableFuture.supplyAsync(() -> BigInteger.valueOf(5));
      CompletableFuture<String> asyncTask2 = CompletableFuture.supplyAsync(() -> "3");

      BigInteger result = allOf(asyncTask1, asyncTask2).thenApplyAsync(fn -> factorial(asyncTask1.join()).add(factorial(new BigInteger(asyncTask2.join()))), Executors.newFixedThreadPool(1))
            .join();

      assertEquals(BigInteger.valueOf(126), result);
   }
}