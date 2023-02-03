package cn.tuyucheng.taketoday.file.content.comparison;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompareFileContentsByBytesUnitTest {

	static Path path1 = null;
	static Path path2 = null;

	@BeforeAll
	static void setup() throws IOException {
		path1 = Files.createTempFile("file1Test", ".txt");
		path2 = Files.createTempFile("file2Test", ".txt");
	}

	@Test
	void whenFirstFileShorter_thenPositionInSecondFile() throws IOException {
		Files.writeString(path1, "testing");
		Files.writeString(path2, "testing1");

		assertEquals(8, CompareFileContents.filesCompareByByte(path1, path2));
	}

	@Test
	void whenSecondFileShorter_thenPositionInFirstFile() throws IOException {
		Files.writeString(path1, "testing1");
		Files.writeString(path2, "testing");

		assertEquals(8, CompareFileContents.filesCompareByByte(path1, path2));
	}

	@Test
	void whenFilesIdentical_thenSuccess() throws IOException {
		Files.writeString(path1, "testing");
		Files.writeString(path2, "testing");

		assertEquals(-1, CompareFileContents.filesCompareByByte(path1, path2));
	}

	@Test
	void whenFilesDifferent_thenPosition() throws IOException {
		Files.writeString(path1, "tesXing");
		Files.writeString(path2, "testing");

		assertEquals(4, CompareFileContents.filesCompareByByte(path1, path2));
	}

	@Test
	void whenBothFilesEmpty_thenEqual() throws IOException {
		Files.writeString(path1, "");
		Files.writeString(path2, "");

		assertEquals(-1, CompareFileContents.filesCompareByByte(path1, path2));
	}

	@Test
	void whenFirstEmpty_thenPositionFirst() throws IOException {
		Files.writeString(path1, "");
		Files.writeString(path2, "test");

		assertEquals(1, CompareFileContents.filesCompareByByte(path1, path2));
	}

	@Test
	void whenSecondEmpty_thenPositionFirst() throws IOException {
		Files.writeString(path1, "test");
		Files.writeString(path2, "");

		assertEquals(1, CompareFileContents.filesCompareByByte(path1, path2));
	}

	@AfterAll
	static void shutDown() {
		path1.toFile().deleteOnExit();
		path2.toFile().deleteOnExit();
	}
}