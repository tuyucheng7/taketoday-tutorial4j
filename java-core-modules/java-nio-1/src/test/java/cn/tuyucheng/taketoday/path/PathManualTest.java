package cn.tuyucheng.taketoday.path;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PathManualTest {

   private static final String HOME = System.getProperty("user.home");

   // creating a path
   @Test
   public void givenPathString_whenCreatesPathObject_thenCorrect() {
      Path p = Paths.get("/articles/tuyucheng");
      assertEquals("\\articles\\tuyucheng", p.toString());

   }

   @Test
   public void givenPathParts_whenCreatesPathObject_thenCorrect() {
      Path p = Paths.get("/articles", "tuyucheng");
      assertEquals("\\articles\\tuyucheng", p.toString());

   }

   // retrieving path info
   @Test
   public void givenPath_whenRetrievesFileName_thenCorrect() {
      Path p = Paths.get("/articles/tuyucheng/logs");
      assertEquals("logs", p.getFileName().toString());
   }

   @Test
   public void givenPath_whenRetrievesNameByIndex_thenCorrect() {
      Path p = Paths.get("/articles/tuyucheng/logs");
      assertEquals("articles", p.getName(0).toString());
      assertEquals("tuyucheng", p.getName(1).toString());
      assertEquals("logs", p.getName(2).toString());
   }

   @Test
   public void givenPath_whenCountsParts_thenCorrect() {
      Path p = Paths.get("/articles/tuyucheng/logs");
      assertEquals(3, p.getNameCount());
   }

   @Test
   public void givenPath_whenCanRetrieveSubsequenceByIndex_thenCorrect() {
      Path p = Paths.get("/articles/tuyucheng/logs");
      assertEquals("articles", p.subpath(0, 1).toString());
      assertEquals("articles\\tuyucheng", p.subpath(0, 2).toString());
      assertEquals("articles\\tuyucheng\\logs", p.subpath(0, 3).toString());
      assertEquals("tuyucheng", p.subpath(1, 2).toString());
      assertEquals("tuyucheng\\logs", p.subpath(1, 3).toString());
      assertEquals("logs", p.subpath(2, 3).toString());
   }

   @Test
   public void givenPath_whenRetrievesParent_thenCorrect() {
      Path p1 = Paths.get("/articles/tuyucheng/logs");
      Path p2 = Paths.get("/articles/tuyucheng");
      Path p3 = Paths.get("/articles");
      Path p4 = Paths.get("/");

      assertEquals("\\articles\\tuyucheng", p1.getParent().toString());
      assertEquals("\\articles", p2.getParent().toString());
      assertEquals("\\", p3.getParent().toString());
      assertEquals(null, p4.getParent());
   }

   @Test
   public void givenPath_whenRetrievesRoot_thenCorrect() {
      Path p1 = Paths.get("/articles/tuyucheng/logs");
      Path p2 = Paths.get("c:/articles/tuyucheng/logs");

      assertEquals("\\", p1.getRoot().toString());
      assertEquals("c:\\", p2.getRoot().toString());
   }

   // removing redundancies from path
   @Test
   public void givenPath_whenRemovesRedundancies_thenCorrect1() {
      Path p = Paths.get("/home/./tuyucheng/articles");
      p = p.normalize();
      assertEquals("\\home\\tuyucheng\\articles", p.toString());
   }

   @Test
   public void givenPath_whenRemovesRedundancies_thenCorrect2() {
      Path p = Paths.get("/home/tuyucheng/../articles");
      p = p.normalize();
      assertEquals("\\home\\articles", p.toString());
   }

   // converting a path
   @Test
   public void givenPath_whenConvertsToBrowseablePath_thenCorrect() {
      Path p = Paths.get("/home/tuyucheng/articles.html");
      URI uri = p.toUri();
      assertEquals("file:///E:/home/tuyucheng/articles.html", uri.toString());
   }

   @Test
   public void givenPath_whenConvertsToAbsolutePath_thenCorrect() {
      Path p = Paths.get("/home/tuyucheng/articles.html");
      assertEquals("E:\\home\\tuyucheng\\articles.html", p.toAbsolutePath().toString());
   }

   @Test
   public void givenAbsolutePath_whenRetainsAsAbsolute_thenCorrect() {
      Path p = Paths.get("E:\\home\\tuyucheng\\articles.html");
      assertEquals("E:\\home\\tuyucheng\\articles.html", p.toAbsolutePath().toString());
   }

   @Test
   public void givenExistingPath_whenGetsRealPathToFile_thenCorrect() throws IOException {
      Path p = Paths.get(HOME);
      assertEquals(HOME, p.toRealPath().toString());
   }

   @Test(expected = NoSuchFileException.class)
   public void givenInExistentPath_whenFailsToConvert_thenCorrect() throws IOException {
      Path p = Paths.get("E:\\home\\tuyucheng\\articles.html");

      p.toRealPath();
   }

   // joining paths
   @Test
   public void givenTwoPaths_whenJoinsAndResolves_thenCorrect() throws IOException {
      Path p = Paths.get("/tuyucheng/articles");
      assertEquals("\\tuyucheng\\articles\\java", p.resolve("java").toString());
   }

   @Test
   public void givenAbsolutePath_whenResolutionRetainsIt_thenCorrect() throws IOException {
      Path p = Paths.get("/tuyucheng/articles");
      assertEquals("C:\\tuyucheng\\articles\\java", p.resolve("C:\\tuyucheng\\articles\\java").toString());
   }

   @Test
   public void givenPathWithRoot_whenResolutionRetainsIt_thenCorrect2() throws IOException {
      Path p = Paths.get("/tuyucheng/articles");
      assertEquals("\\java", p.resolve("/java").toString());
   }

   // creating a path between 2 paths
   @Test
   public void givenSiblingPaths_whenCreatesPathToOther_thenCorrect() throws IOException {
      Path p1 = Paths.get("articles");
      Path p2 = Paths.get("authors");
      assertEquals("..\\authors", p1.relativize(p2).toString());
      assertEquals("..\\articles", p2.relativize(p1).toString());
   }

   @Test
   public void givenNonSiblingPaths_whenCreatesPathToOther_thenCorrect() throws IOException {
      Path p1 = Paths.get("/tuyucheng");
      Path p2 = Paths.get("/tuyucheng/authors/articles");
      assertEquals("authors\\articles", p1.relativize(p2).toString());
      assertEquals("..\\..", p2.relativize(p1).toString());
   }

   // comparing 2 paths
   @Test
   public void givenTwoPaths_whenTestsEquality_thenCorrect() throws IOException {
      Path p1 = Paths.get("/tuyucheng/articles");
      Path p2 = Paths.get("/tuyucheng/articles");
      Path p3 = Paths.get("/tuyucheng/authors");

      assertTrue(p1.equals(p2));
      assertFalse(p1.equals(p3));
   }

   @Test
   public void givenPath_whenInspectsStart_thenCorrect() {
      Path p1 = Paths.get("/tuyucheng/articles");
      assertTrue(p1.startsWith("/tuyucheng"));
   }

   @Test
   public void givenPath_whenInspectsEnd_thenCorrect() {
      Path p1 = Paths.get("/tuyucheng/articles");
      assertTrue(p1.endsWith("articles"));
   }
}
