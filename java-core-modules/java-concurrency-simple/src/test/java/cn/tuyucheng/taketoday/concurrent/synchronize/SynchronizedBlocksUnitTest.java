package cn.tuyucheng.taketoday.concurrent.synchronize;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SynchronizedBlocksUnitTest {

   @Test
   void givenMultiThread_whenBlockSync() throws InterruptedException {
      ExecutorService service = Executors.newFixedThreadPool(3);
      SynchronizedBlocks synchronizedBlocks = new SynchronizedBlocks();

      IntStream.range(0, 1000)
            .forEach(count -> service.submit(synchronizedBlocks::performSynchronisedTask));
      service.awaitTermination(500, TimeUnit.MILLISECONDS);

      assertEquals(1000, synchronizedBlocks.getCount());
   }

   @Test
   void givenMultiThread_whenStaticSyncBlock() throws InterruptedException {
      ExecutorService service = Executors.newCachedThreadPool();

      IntStream.range(0, 1000)
            .forEach(count -> service.submit(SynchronizedBlocks::performStaticSyncTask));
      service.awaitTermination(500, TimeUnit.MILLISECONDS);

      assertEquals(1000, SynchronizedBlocks.getStaticCount());
   }

   @Test
   void givenHoldingTheLock_whenReentrant_thenCanAcquireItAgain() {
      Object lock = new Object();
      synchronized (lock) {
         System.out.println("First time acquiring it");

         synchronized (lock) {
            System.out.println("Entering again");

            synchronized (lock) {
               System.out.println("And again");
            }
         }
      }
   }
}