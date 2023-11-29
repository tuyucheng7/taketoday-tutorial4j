package cn.tuyucheng.taketoday.resources;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.resource.New;
import org.junitpioneer.jupiter.resource.Shared;
import org.junitpioneer.jupiter.resource.TemporaryDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InjectResourcesUnitTest {

   @Test
   void inject_temDir_test1(@New(TemporaryDirectory.class) Path tempDir) {
      assertTrue(Files.exists(tempDir));
   }

   @Test
   void inject_temDir_test2(@New(TemporaryDirectory.class) Path tempDir) {
      // This temporary directory is different to the first one.
      assertTrue(Files.exists(tempDir));
   }

   @Test
   void whenSpecifiesTemDirPrefix_thenCorrect(@New(value = TemporaryDirectory.class, arguments = "customPrefix") Path tempDir) {
      Path rootTempDir = Paths.get(System.getProperty("java.io.tmpdir"));
      assertThat(rootTempDir.relativize(tempDir).toFile().getName())
            .startsWith("customPrefix");
   }

   // === shard resource ===

   @Test
   void shared_resource_test1(@Shared(factory = TemporaryDirectory.class, name = "sharedTempDir") Path sharedTempDir) throws IOException {
      Files.createFile(sharedTempDir.resolve("test.txt"));

      assertTrue(Files.exists(sharedTempDir));
      assertTrue(Files.exists(sharedTempDir.resolve("test.txt")));
   }

   @Test
   void shared_resource_test2(@Shared(factory = TemporaryDirectory.class, name = "sharedTempDir") Path sharedTempDir) {
      // sharedTempDir is shared with the temp dir of the same name in test "shared_resource_test1", so any created subdirectories and files will be shared.
      assertTrue(Files.exists(sharedTempDir));
      assertTrue(Files.exists(sharedTempDir.resolve("test.txt")));
   }

   // === shared multiple resource ===

   @Test
   void firstSharedResource1(@Shared(factory = TemporaryDirectory.class, name = "first") Path first) throws IOException {
      Files.createFile(first.resolve("test.txt"));

      assertTrue(Files.exists(first));
      assertTrue(Files.exists(first.resolve("test.txt")));
   }

   @Test
   void firstSharedResource2(@Shared(factory = TemporaryDirectory.class, name = "first") Path first) throws IOException {
      assertTrue(Files.exists(first));
      assertTrue(Files.exists(first.resolve("test.txt")));
   }

   @Test
   void secondSharedResource(@Shared(factory = TemporaryDirectory.class, name = "second") Path second) {
      // This shared resource is different!
      assertTrue(Files.exists(second));
      assertFalse(Files.exists(second.resolve("test.txt")));
   }
}