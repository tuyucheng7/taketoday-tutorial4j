package cn.tuyucheng.taketoday.symlink;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SymLinkExampleManualTest {

   @Test
   public void whenUsingFiles_thenCreateSymbolicLink() throws IOException {
      SymLinkExample example = new SymLinkExample();
      Path filePath = example.createTextFile();
      Path linkPath = Paths.get(".", "symbolic_link.txt");
      example.createSymbolicLink(linkPath, filePath);
      assertTrue(Files.isSymbolicLink(linkPath));
   }

   @Test
   public void whenUsingFiles_thenCreateHardLink() throws IOException {
      SymLinkExample example = new SymLinkExample();
      Path filePath = example.createTextFile();
      Path linkPath = Paths.get(".", "hard_link.txt");
      example.createHardLink(linkPath, filePath);
      assertFalse(Files.isSymbolicLink(linkPath));
      assertEquals(filePath.toFile()
                  .length(),
            linkPath.toFile()
                  .length());
   }

}
