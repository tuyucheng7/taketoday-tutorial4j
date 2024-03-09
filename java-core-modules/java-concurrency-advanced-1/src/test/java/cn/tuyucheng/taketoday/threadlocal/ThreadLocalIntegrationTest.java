package cn.tuyucheng.taketoday.threadlocal;


import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ThreadLocalIntegrationTest {
   @Test
   public void givenThreadThatStoresContextInAMap_whenStartThread_thenShouldSetContextForBothUsers() throws ExecutionException, InterruptedException {
      // when
      SharedMapWithUserContext firstUser = new SharedMapWithUserContext(1);
      SharedMapWithUserContext secondUser = new SharedMapWithUserContext(2);
      new Thread(firstUser).start();
      new Thread(secondUser).start();

      Thread.sleep(3000);
      // then
      assertEquals(SharedMapWithUserContext.userContextPerUserId.size(), 2);
   }

   @Test
   public void givenThreadThatStoresContextInThreadLocal_whenStartThread_thenShouldStoreContextInThreadLocal() throws ExecutionException, InterruptedException {
      // when
      ThreadLocalWithUserContext firstUser = new ThreadLocalWithUserContext(1);
      ThreadLocalWithUserContext secondUser = new ThreadLocalWithUserContext(2);
      new Thread(firstUser).start();
      new Thread(secondUser).start();

      Thread.sleep(3000);
   }

}
