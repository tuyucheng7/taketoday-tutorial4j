package cn.tuyucheng.taketoday.rules;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RulesUnitTest {

	private static final Logger LOG = LoggerFactory.getLogger(RulesUnitTest.class);

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Rule
	public TestName name = new TestName();

	@Rule
	public Timeout globalTimeout = Timeout.seconds(10);

	@Rule
	public final ErrorCollector errorCollector = new ErrorCollector();

	@Rule
	public DisableOnDebug disableTimeout = new DisableOnDebug(Timeout.seconds(30));

	@Rule
	public TestMethodNameLogger testLogger = new TestMethodNameLogger();

	@Test
	public void givenTempFolderRule_whenNewFile_thenFileIsCreated() throws IOException {
		File testFile = tmpFolder.newFile("test-file.txt");

		assertTrue("The file should have been created: ", testFile.isFile());
		assertEquals("Temp folder and test file should match: ", tmpFolder.getRoot(), testFile.getParentFile());
	}

	@Test
	public void givenIllegalArgument_whenExceptionThrown_thenMessageAndCauseMatches() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectCause(isA(NullPointerException.class));
		thrown.expectMessage("This is illegal");

		throw new IllegalArgumentException("This is illegal", new NullPointerException());
	}

	@Test
	public void givenAddition_whenPrintingTestName_thenTestNameIsDisplayed() {
		LOG.info("Executing: {}", name.getMethodName());
		assertEquals("givenAddition_whenPrintingTestName_thenTestNameIsDisplayed", name.getMethodName());
	}

	@Ignore
	@Test
	public void givenLongRunningTest_whenTimout_thenTestFails() throws InterruptedException {
		TimeUnit.SECONDS.sleep(20);
	}

	@Ignore
	@Test
	public void givenMultipleErrors_whenTestRuns_thenCollectorReportsErrors() {
		errorCollector.addError(new Throwable("First thing went wrong!"));
		errorCollector.addError(new Throwable("Another thing went wrong!"));

		errorCollector.checkThat("Hello World", not(containsString("ERROR!")));
	}
}