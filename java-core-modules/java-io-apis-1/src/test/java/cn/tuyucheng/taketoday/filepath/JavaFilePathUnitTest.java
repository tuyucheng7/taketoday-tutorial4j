package cn.tuyucheng.taketoday.filepath;

import org.junit.Assume;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JavaFilePathUnitTest {

   private static String userDir;

   @BeforeAll
   public static void createFilesAndFolders() throws IOException {
      userDir = System.getProperty("user.dir");

      new File(userDir + "/tuyucheng/foo").mkdirs();
      new File(userDir + "/tuyucheng/bar/baz").mkdirs();
      new File(userDir + "/tuyucheng/foo/foo-one.txt").createNewFile();
      new File(userDir + "/tuyucheng/foo/foo-two.txt").createNewFile();
      new File(userDir + "/tuyucheng/bar/bar-one.txt").createNewFile();
      new File(userDir + "/tuyucheng/bar/bar-two.txt").createNewFile();
      new File(userDir + "/tuyucheng/bar/baz/baz-one.txt").createNewFile();
      new File(userDir + "/tuyucheng/bar/baz/baz-two.txt").createNewFile();

   }

   @Test
   public void whenPathResolved_thenSuccess() {
      File file = new File("tuyucheng/foo/foo-one.txt");
      String expectedPath = isWindows() ? "tuyucheng\\foo\\foo-one.txt" : "tuyucheng/foo/foo-one.txt";
      String actualPath = file.getPath();
      assertEquals(expectedPath, actualPath);
   }

   @Test
   public void whenAbsolutePathResolved_thenSuccess() {
      File file = new File("tuyucheng/foo/foo-one.txt");
      String expectedPath = isWindows() ? userDir + "\\tuyucheng\\foo\\foo-one.txt" : userDir + "/tuyucheng/foo/foo-one.txt";
      String actualPath = file.getAbsolutePath();
      assertEquals(expectedPath, actualPath);
   }

   @Test
   public void whenAbsolutePathWithShorthandResolved_thenSuccess() {
      File file = new File("tuyucheng/bar/baz/../bar-one.txt");
      String expectedPath = isWindows() ? userDir + "\\tuyucheng\\bar\\baz\\..\\bar-one.txt" : userDir + "/tuyucheng/bar/baz/../bar-one.txt";
      String actualPath = file.getAbsolutePath();
      assertEquals(expectedPath, actualPath);
   }

   @Test
   public void whenCanonicalPathWithShorthandResolved_thenSuccess() throws IOException {
      File file = new File("tuyucheng/bar/baz/../bar-one.txt");
      String expectedPath = isWindows() ? userDir + "\\tuyucheng\\bar\\bar-one.txt" : userDir + "/tuyucheng/bar/bar-one.txt";
      String actualPath = file.getCanonicalPath();
      assertEquals(expectedPath, actualPath);
   }

   @Test
   public void whenCanonicalPathWithDotShorthandResolved_thenSuccess() throws IOException {
      File file = new File("tuyucheng/bar/baz/./baz-one.txt");
      String expectedPath = isWindows() ? userDir + "\\tuyucheng\\bar\\baz\\baz-one.txt" : userDir + "/tuyucheng/bar/baz/baz-one.txt";
      String actualPath = file.getCanonicalPath();
      assertEquals(expectedPath, actualPath);
   }

   @Test(expected = IOException.class)
   public void givenWindowsOs_whenCanonicalPathWithWildcard_thenIOException() throws IOException {
      Assume.assumeTrue(isWindows());
      new File("*").getCanonicalPath();
   }

   @AfterAll
   public static void deleteFilesAndFolders() {
      File tuyuchengDir = new File(userDir + "/tuyucheng");
      deleteRecursively(tuyuchengDir);
   }

   private static void deleteRecursively(File dir) {
      for (File f : dir.listFiles()) {
         if (f.isDirectory()) {
            deleteRecursively(f);
         }
         f.delete();
      }
      dir.delete();
   }

   private static boolean isWindows() {
      String osName = System.getProperty("os.name");
      return osName.contains("Windows");
   }

}
