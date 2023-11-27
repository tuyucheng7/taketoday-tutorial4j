package cn.tuyucheng.taketoday.async;

import cn.tuyucheng.taketoday.async.config.SpringAsyncConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringAsyncConfig.class}, loader = AnnotationConfigContextLoader.class)
class AsyncAnnotationExampleIntegrationTest {

   @Autowired
   private AsyncComponent asyncAnnotationExample;

   // tests

   @Test
   void testAsyncAnnotationForMethodsWithVoidReturnType() {
      System.out.println(STR."Start - invoking an asynchronous method. \{Thread.currentThread().getName()}");
      asyncAnnotationExample.asyncMethodWithVoidReturnType();
      System.out.println("End - invoking an asynchronous method. ");
   }

   @Test
   void testAsyncAnnotationForMethodsWithReturnType() throws InterruptedException, ExecutionException {
      System.out.println(STR."Start - invoking an asynchronous method. \{Thread.currentThread().getName()}");
      final Future<String> future = asyncAnnotationExample.asyncMethodWithReturnType();

      while (true) {
         if (future.isDone()) {
            System.out.println(STR."Result from asynchronous process - \{future.get()}");
            break;
         }
         System.out.println("Continue doing something else. ");
         Thread.sleep(1000);
      }
   }

   @Test
   void testAsyncAnnotationForMethodsWithConfiguredExecutor() {
      System.out.println("Start - invoking an asynchronous method. ");
      asyncAnnotationExample.asyncMethodWithConfiguredExecutor();
      System.out.println("End - invoking an asynchronous method. ");
   }

   @Test
   void testAsyncAnnotationForMethodsWithException() throws Exception {
      System.out.println("Start - invoking an asynchronous method. ");
      asyncAnnotationExample.asyncMethodWithExceptions();
      System.out.println("End - invoking an asynchronous method. ");
   }
}