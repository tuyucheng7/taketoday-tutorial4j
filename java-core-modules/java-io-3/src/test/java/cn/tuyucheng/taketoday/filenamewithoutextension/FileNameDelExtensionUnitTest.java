package cn.tuyucheng.taketoday.filenamewithoutextension;

import com.google.common.io.Files;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class FileNameDelExtensionUnitTest {

	@Test
	public void givenDotFileWithoutExt_whenCallGuavaMethod_thenCannotGetDesiredResult() {
		// negative assertion
		assertNotEquals(".tuyucheng", Files.getNameWithoutExtension(".tuyucheng"));
	}

	@Test
	public void givenFileWithoutMultipleExt_whenCallGuavaMethod_thenCannotRemoveAllExtensions() {
		// negative assertion
		assertNotEquals("tuyucheng", Files.getNameWithoutExtension("tuyucheng.tar.gz"));
	}

	@Test
	public void givenDotFileWithoutExt_whenCallApacheCommonsMethod_thenCannotGetDesiredResult() {
		// negative assertion
		assertNotEquals(".tuyucheng", FilenameUtils.removeExtension(".tuyucheng"));
	}

	@Test
	public void givenFileWithoutMultipleExt_whenCallApacheCommonsMethod_thenCannotRemoveAllExtensions() {
		// negative assertion
		assertNotEquals("tuyucheng", FilenameUtils.removeExtension("tuyucheng.tar.gz"));
	}

	@Test
	public void givenFilenameNoExt_whenCallFilenameUtilMethod_thenGetExpectedFilename() {
		assertEquals("tuyucheng", MyFilenameUtil.removeFileExtension("tuyucheng", true));
		assertEquals("tuyucheng", MyFilenameUtil.removeFileExtension("tuyucheng", false));
	}

	@Test
	public void givenSingleExt_whenCallFilenameUtilMethod_thenGetExpectedFilename() {
		assertEquals("tuyucheng", MyFilenameUtil.removeFileExtension("tuyucheng.txt", true));
		assertEquals("tuyucheng", MyFilenameUtil.removeFileExtension("tuyucheng.txt", false));
	}


	@Test
	public void givenDotFile_whenCallFilenameUtilMethod_thenGetExpectedFilename() {
		assertEquals(".tuyucheng", MyFilenameUtil.removeFileExtension(".tuyucheng", true));
		assertEquals(".tuyucheng", MyFilenameUtil.removeFileExtension(".tuyucheng", false));
	}

	@Test
	public void givenDotFileWithExt_whenCallFilenameUtilMethod_thenGetExpectedFilename() {
		assertEquals(".tuyucheng", MyFilenameUtil.removeFileExtension(".tuyucheng.conf", true));
		assertEquals(".tuyucheng", MyFilenameUtil.removeFileExtension(".tuyucheng.conf", false));
	}

	@Test
	public void givenDoubleExt_whenCallFilenameUtilMethod_thenGetExpectedFilename() {
		assertEquals("tuyucheng", MyFilenameUtil.removeFileExtension("tuyucheng.tar.gz", true));
		assertEquals("tuyucheng.tar", MyFilenameUtil.removeFileExtension("tuyucheng.tar.gz", false));
	}

	@Test
	public void givenDotFileWithDoubleExt_whenCallFilenameUtilMethod_thenGetExpectedFilename() {
		assertEquals(".tuyucheng", MyFilenameUtil.removeFileExtension(".tuyucheng.conf.bak", true));
		assertEquals(".tuyucheng.conf", MyFilenameUtil.removeFileExtension(".tuyucheng.conf.bak", false));
	}
}
