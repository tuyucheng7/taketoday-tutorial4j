package cn.tuyucheng.taketoday.concurrent.synchronizestatic;

import cn.tuyucheng.taketoday.concurrent.synchronizestatic.synchronizedmethod.Employee;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * The tests in this class show the output of creating multiple
 * types of Employee classes in the <code>synchronizedstatic</code>
 * package. When not synchronized the out will not be sequential;
 * when it is synchronized the output will be in sequential.
 */
public class SychronizeStaticDataUnitTest {
   private final Executor pool = Executors.newFixedThreadPool(4);

   private final int numberToTest = 100;

   @Test
   public void whenNotSynchronized_thenDataOutOfOrder() {

      System.out.println("No synchronization");

      for (int i = 0; i < numberToTest; i++) {
         int finalI = i;
         pool.execute(() -> {
            new cn.tuyucheng.taketoday.concurrent.synchronizestatic.none.Employee(finalI, "John", "Smith");
         });
      }
   }

   @Test
   public void whenSynchronizedMethod_thenDataInOrder() {

      System.out.println("Synchronization with synchronized method");

      for (int i = 0; i < numberToTest; i++) {
         int finalI = i;
         pool.execute(() -> {
            new Employee(finalI, "John", "Smith");
         });
      }
   }

   @Test
   public void whenSynchronizedClass_thenDataInOrder() {

      System.out.println("Synchronization with synchronized block on class");

      for (int i = 0; i < numberToTest; i++) {
         int finalI = i;
         pool.execute(() -> {
            new cn.tuyucheng.taketoday.concurrent.synchronizestatic.synchronizedclass.Employee(finalI, "John", "Smith");
         });
      }
   }

   @Test
   public void whenSynchronizedBlock_thenDataInOrder() {

      System.out.println("Synchronization with synchronized block on a private object");

      for (int i = 0; i < numberToTest; i++) {
         int finalI = i;
         pool.execute(() -> {
            new cn.tuyucheng.taketoday.concurrent.synchronizestatic.synchronizedblock.Employee(finalI, "John", "Smith");
         });
      }
   }

   @Test
   public void whenAtomicInteger_thenDataInOrder() {
      // Not straight forward to test this because we cannot log/print
      // and increment values in a synchronized fashion like other
      // tests
   }

   @Test
   public void whenReentrantLock_thenDataInOrder() {

      System.out.println("Synchronization with ReentrantLock");

      for (int i = 0; i < numberToTest; i++) {
         int finalI = i;
         pool.execute(() -> {
            new cn.tuyucheng.taketoday.concurrent.synchronizestatic.reentrantlock.Employee(finalI, "John", "Smith");
         });
      }
   }
}
