package cn.tuyucheng.taketoday.extension;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class ExtensionUnitTest {
   private Extension extension = new Extension();

   @Test
   public void getExtension_whenApacheCommonIO_thenExtensionIsTrue() {
      String expectedExtension = "txt";
      String actualExtension = extension.getExtensionByApacheCommonLib("jarvis.txt");
      Assertions.assertEquals(expectedExtension, actualExtension);
   }

   @Test
   public void getExtension_whenStringHandle_thenExtensionIsTrue() {
      String expectedExtension = "java";
      Optional<String> actualExtension = extension.getExtensionByStringHandling("Demo.java");
      Assertions.assertEquals(expectedExtension, actualExtension.get());
   }

   @Test
   public void getExtension_whenGuava_thenExtensionIsTrue() {
      String expectedExtension = "class";
      String actualExtension = extension.getExtensionByGuava("tuyucheng/Demo.class");
      Assertions.assertEquals(expectedExtension, actualExtension);
   }
}
