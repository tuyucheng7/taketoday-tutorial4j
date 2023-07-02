package cn.tuyucheng.taketoday.concurrent.completablefuture.threadpool;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class CompletableFutureThreadPoolUnitTest {

   @Test
   void whenUsingNonAsync_thenUsesMainThread() throws ExecutionException, InterruptedException {
      CompletableFuture<String> name = CompletableFuture.supplyAsync(() -> "Tuyucheng");

      CompletableFuture<Integer> nameLength = name.thenApply(value -> {
         printCurrentThread();
         return value.length();
      });

      assertThat(nameLength.get()).isEqualTo(9);
   }

   @Test
   void whenUsingNonAsync_thenUsesCallersThread() throws InterruptedException {
      Runnable test = () -> {
         CompletableFuture<String> name = CompletableFuture.supplyAsync(() -> "Tuyucheng");

         CompletableFuture<Integer> nameLength = name.thenApply(value -> {
            printCurrentThread();
            return value.length();
         });

         try {
            assertThat(nameLength.get()).isEqualTo(9);
         } catch (Exception e) {
            fail(e.getMessage());
         }
      };

      new Thread(test, "test-thread").start();
      Thread.sleep(100L);
   }

   @Test
   void whenUsingAsync_thenUsesCommonPool() throws ExecutionException, InterruptedException {
      CompletableFuture<String> name = CompletableFuture.supplyAsync(() -> "Tuyucheng");

      CompletableFuture<Integer> nameLength = name.thenApplyAsync(value -> {
         printCurrentThread();
         return value.length();
      });

      assertThat(nameLength.get()).isEqualTo(9);
   }

   @Test
   void whenUsingAsync_thenUsesCustomExecutor() throws ExecutionException, InterruptedException {
      Executor testExecutor = Executors.newFixedThreadPool(5);
      CompletableFuture<String> name = CompletableFuture.supplyAsync(() -> "Tuyucheng");

      CompletableFuture<Integer> nameLength = name.thenApplyAsync(value -> {
         printCurrentThread();
         return value.length();
      }, testExecutor);

      assertThat(nameLength.get()).isEqualTo(9);
   }

   @Test
   void whenOverridingDefaultThreadPool_thenUsesCustomExecutor() throws ExecutionException, InterruptedException {
      CompletableFuture<String> name = CustomCompletableFuture.supplyAsync(() -> "Tuyucheng");

      CompletableFuture<Integer> nameLength = name.thenApplyAsync(value -> {
         printCurrentThread();
         return value.length();
      });

      assertThat(nameLength.get()).isEqualTo(9);
   }

   private static void printCurrentThread() {
      System.out.println(Thread.currentThread().getName());
   }
}