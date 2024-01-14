package cn.tuyucheng.taketoday.manifest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExecuteJarFileUnitTest {

   private static final String ERROR_MESSAGE = "no main manifest attribute, in example.jar\n";
   private static final String SUCCESS_MESSAGE = "AppExample executed!\n";

   @Test
   public final void givenDefaultManifest_whenManifestAttributeIsNotPresent_thenGetErrorMessage() {
      String output = ExecuteJarFile.executeJarWithoutManifestAttribute();
      assertEquals(ERROR_MESSAGE, output);
   }

   @Test
   public final void givenCustomManifest_whenManifestAttributeIsPresent_thenGetSuccessMessage() {
      String output = ExecuteJarFile.executeJarWithManifestAttribute();
      assertEquals(SUCCESS_MESSAGE, output);
   }

}
