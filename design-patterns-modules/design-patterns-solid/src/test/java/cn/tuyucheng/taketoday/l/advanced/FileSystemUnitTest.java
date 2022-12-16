package cn.tuyucheng.taketoday.l.advanced;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileSystemUnitTest {

	@Test
	void whenCreateReadOnlyFileSystem_thenShouldCreateReadOnlyFileSystem() throws IOException {
		FileSystem fileSystem = new ReadOnlyFileSystem();

		fileSystem.deleteFile("/README.md");

		assertEquals(0, fileSystem.listFiles("/README.md").length);
	}

	@Test
	void whenPassReadOnlyFileSystem_thenDoNothing() throws IOException {
		FileSystem fileSystem = new ReadOnlyFileSystem();

		FilePurgingJob filePurgingJob = new FilePurgingJob(fileSystem);

		filePurgingJob.purgeOldestFile("/README.md");
	}
}