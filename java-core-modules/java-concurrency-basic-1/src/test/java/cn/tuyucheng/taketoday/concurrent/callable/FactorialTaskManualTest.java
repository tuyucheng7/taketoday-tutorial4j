package cn.tuyucheng.taketoday.concurrent.callable;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static junit.framework.Assert.assertEquals;

public class FactorialTaskManualTest {

   private ExecutorService executorService;

   @BeforeEach
   public void setup() {
      executorService = Executors.newSingleThreadExecutor();
   }

   @Test
   public void whenTaskSubmitted_ThenFutureResultObtained() throws ExecutionException, InterruptedException {
      FactorialTask task = new FactorialTask(5);
      Future<Integer> future = executorService.submit(task);
      assertEquals(120, future.get().intValue());
   }

   @Test(expected = ExecutionException.class)
   public void whenException_ThenCallableThrowsIt() throws ExecutionException, InterruptedException {
      FactorialTask task = new FactorialTask(-5);
      Future<Integer> future = executorService.submit(task);
      Integer result = future.get().intValue();
   }

   @Test
   public void whenException_ThenCallableDoesntThrowsItIfGetIsNotCalled() {
      FactorialTask task = new FactorialTask(-5);
      Future<Integer> future = executorService.submit(task);
      assertEquals(false, future.isDone());
   }

   @AfterEach
   public void cleanup() {
      executorService.shutdown();
   }
}
