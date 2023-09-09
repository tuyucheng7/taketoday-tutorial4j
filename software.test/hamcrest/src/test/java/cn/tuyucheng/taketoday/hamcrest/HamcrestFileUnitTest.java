package cn.tuyucheng.taketoday.hamcrest;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.io.FileMatchers.aFileNamed;
import static org.hamcrest.io.FileMatchers.aFileWithAbsolutePath;
import static org.hamcrest.io.FileMatchers.aFileWithCanonicalPath;
import static org.hamcrest.io.FileMatchers.aFileWithSize;
import static org.hamcrest.io.FileMatchers.aReadableFile;
import static org.hamcrest.io.FileMatchers.aWritableFile;
import static org.hamcrest.io.FileMatchers.anExistingDirectory;
import static org.hamcrest.io.FileMatchers.anExistingFile;
import static org.hamcrest.io.FileMatchers.anExistingFileOrDirectory;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

class HamcrestFileUnitTest {

   @Test
   final void whenVerifyingFileName_thenCorrect() {
      File file = new File("src/test/resources/test1.in");

      assertThat(file, aFileNamed(equalToIgnoringCase("test1.in")));
   }

   @Test
   final void whenVerifyingFileOrDirExist_thenCorrect() {
      File file = new File("src/test/resources/test1.in");
      File dir = new File("src/test/resources");

      assertThat(file, anExistingFile());
      assertThat(dir, anExistingDirectory());
      assertThat(file, anExistingFileOrDirectory());
      assertThat(dir, anExistingFileOrDirectory());
   }

   @Test
   final void whenVerifyingFileIsReadableAndWritable_thenCorrect() {
      File file = new File("src/test/resources/test1.in");

      assertThat(file, aReadableFile());
      assertThat(file, aWritableFile());
   }

   @Test
   final void whenVerifyingFileSize_thenCorrect() {
      File file = new File("src/test/resources/test1.in");

      assertThat(file, aFileWithSize(11));
      assertThat(file, aFileWithSize(greaterThan(1L)));
   }

   @Test
   @Disabled("This test is not working on Windows")
   final void whenVerifyingFilePath_thenCorrect() {
      File file = new File("src/test/resources/test1.in");

      assertThat(file, aFileWithCanonicalPath(containsString("src/test/resources")));
      assertThat(file, aFileWithAbsolutePath(containsString("src/test/resources")));
   }
}