package cn.tuyucheng.taketoday.finalization_closeable_cleaner;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class FinalizationCloseableCleanerUnitTest {

   @Test
   public void givenMyFinalizationResource_whenUsingFinalize_thenShouldClean() {
      assertDoesNotThrow(() -> {
         MyFinalizableResourceClass mfr = new MyFinalizableResourceClass();
         mfr.getByteLength();
      });
   }

   @Test
   public void givenMyCleanerResource_whenUsingCleanerAPI_thenShouldClean() {
      assertDoesNotThrow(() -> {
         try (MyCleanerResourceClass myCleanerResourceClass = new MyCleanerResourceClass()) {
            myCleanerResourceClass.useResource();
         }
      });
   }

   @Test
   public void givenCloseableResource_whenUsingTryWith_thenShouldClose() throws IOException {
      int length = 0;
      try (MyCloseableResourceClass mcr = new MyCloseableResourceClass()) {
         length = mcr.getByteLength();
      }
      Assertions.assertEquals(20, length);
   }
}