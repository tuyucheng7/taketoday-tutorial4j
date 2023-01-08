package cn.tuyucheng.taketoday.fileparser;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class FilesReadAllLinesUnitTest {

	protected static final String TEXT_FILENAME = "src/test/resources/sampleTextFile.txt";

	@Test
	public void whenParsingExistingTextFile_thenGetArrayList() throws IOException {
		List<String> lines = FilesReadLinesExample.generateArrayListFromFile(TEXT_FILENAME);
		assertTrue("File does not has 2 lines", lines.size() == 2);
	}
}
