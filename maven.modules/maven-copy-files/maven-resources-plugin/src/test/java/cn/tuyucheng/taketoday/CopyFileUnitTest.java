package cn.tuyucheng.taketoday;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CopyFileUnitTest {

	@Test
	void whenCopyingAFileFromSourceToDestination_thenFileShouldBeInDestination() {
		File destinationFile = new File("target/destination-folder/foo.txt");
		assertTrue(destinationFile.exists());
	}
}