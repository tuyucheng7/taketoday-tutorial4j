package cn.tuyucheng.taketoday.async;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:springAsync-config.xml")
class AsyncWithXMLIntegrationTest {

   @Autowired
   private AsyncComponent asyncAnnotationExample;

   // tests

   @Test
   void testAsyncAnnotationForMethodsWithVoidReturnType() throws InterruptedException {
      System.out.println(STR."Start - invoking an asynchronous method. \{Thread.currentThread().getName()}");
      asyncAnnotationExample.asyncMethodWithVoidReturnType();
      Thread.sleep(2000);
      System.out.println("End - invoking an asynchronous method. ");
   }
}