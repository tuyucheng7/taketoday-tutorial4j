package cn.tuyucheng.taketoday.zipentries;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ZipEntryReaderUnitTest {

   @Test
   public void givenZipFile_thenReadEntriesAndValidateContent() throws URISyntaxException, IOException {
      Path zipFilePath = Paths.get(getClass().getClassLoader().getResource("zipFile.zip").toURI());
      ZipEntryReader.readZipEntries(zipFilePath.toString());
   }
}
