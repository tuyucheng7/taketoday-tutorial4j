package cn.tuyucheng.taketoday.finalize;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class FinalizeUnitTest {
   @Test
   public void whenGC_thenFinalizerExecuted() throws IOException {
      String firstLine = new Finalizable().readFirstLine();
      Assertions.assertEquals("tuyucheng.com", firstLine);
      System.gc();
   }

   @Test
   public void whenTryWResourcesExits_thenResourceClosed() throws IOException {
      try (CloseableResource resource = new CloseableResource()) {
         String firstLine = resource.readFirstLine();
         Assertions.assertEquals("tuyucheng.com", firstLine);
      }
   }
}
