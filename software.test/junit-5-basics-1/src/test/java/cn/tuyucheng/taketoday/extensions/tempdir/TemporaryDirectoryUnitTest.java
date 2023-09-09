package cn.tuyucheng.taketoday.extensions.tempdir;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemporaryDirectoryUnitTest {

	@Test
	void givenTestMethodWithTempDirectoryPath_whenWriteToFile_thenContentIsCorrect(@TempDir Path tempDir) throws IOException {
		Path numbers = tempDir.resolve("numbers.txt");

		List<String> lines = Arrays.asList("1", "2", "3");
		Files.write(numbers, lines);

		assertAll(
			() -> assertTrue(Files.exists(numbers), "File should exist"),
			() -> assertLinesMatch(lines, Files.readAllLines(numbers)));
	}

	@TempDir
	File anotherTempDir;

	@Test
	void givenFieldWithTempDirectoryFile_whenWriteToFile_thenContentIsCorrect() throws IOException {
		assertTrue(this.anotherTempDir.isDirectory(), "Should be a directory ");

		File letters = new File(anotherTempDir, "letters.txt");
		List<String> lines = Arrays.asList("x", "y", "z");

		Files.write(letters.toPath(), lines);

		assertAll(
			() -> assertTrue(Files.exists(letters.toPath()), "File should exist"),
			() -> assertLinesMatch(lines, Files.readAllLines(letters.toPath())));
	}
}