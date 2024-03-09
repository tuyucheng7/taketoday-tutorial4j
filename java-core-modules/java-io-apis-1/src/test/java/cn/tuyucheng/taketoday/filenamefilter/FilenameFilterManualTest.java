package cn.tuyucheng.taketoday.filenamefilter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FilenameFilterManualTest {

   private static File directory;

   @BeforeAll
   public static void setupClass() {
      directory = new File(FilenameFilterManualTest.class.getClassLoader()
            .getResource("fileNameFilterManualTestFolder")
            .getFile());
   }

   @Test
   public void whenFilteringFilesEndingWithJson_thenEqualExpectedFiles() {
      FilenameFilter filter = (dir, name) -> name.endsWith(".json");

      String[] expectedFiles = {"people.json", "students.json"};
      String[] actualFiles = directory.list(filter);

      Assertions.assertArrayEquals(expectedFiles, actualFiles);
   }

   @Test
   public void whenFilteringFilesEndingWithXml_thenEqualExpectedFiles() {
      Predicate<String> predicate = (name) -> name.endsWith(".xml");

      String[] expectedFiles = {"teachers.xml", "workers.xml"};
      List<String> files = Arrays.stream(directory.list())
            .filter(predicate)
            .collect(Collectors.toList());
      String[] actualFiles = files.toArray(new String[files.size()]);

      Assertions.assertArrayEquals(expectedFiles, actualFiles);
   }

}
