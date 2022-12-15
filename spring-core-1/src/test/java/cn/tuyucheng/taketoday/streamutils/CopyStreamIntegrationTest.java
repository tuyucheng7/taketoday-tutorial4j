package cn.tuyucheng.taketoday.streamutils;

import org.junit.jupiter.api.Test;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static cn.tuyucheng.taketoday.streamutils.CopyStream.getStringFromInputStream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CopyStreamIntegrationTest {

	@Test
	void whenCopyInputStreamToOutputStream_thenCorrect() throws IOException {
		String inputFileName = "src/test/resources/input.txt";
		String outputFileName = "src/test/resources/output.txt";
		File outputFile = new File(outputFileName);
		InputStream in = new FileInputStream(inputFileName);
		OutputStream out = new FileOutputStream(outputFileName);

		StreamUtils.copy(in, out);

		assertTrue(outputFile.exists());
		String inputFileContent = getStringFromInputStream(new FileInputStream(inputFileName));
		String outputFileContent = getStringFromInputStream(new FileInputStream(outputFileName));
		assertEquals(inputFileContent, outputFileContent);
	}

	@Test
	void whenCopyRangeOfInputStreamToOutputStream_thenCorrect() throws IOException {
		String inputFileName = "src/test/resources/input.txt";
		String outputFileName = "src/test/resources/output.txt";
		File outputFile = new File(outputFileName);
		InputStream in = new FileInputStream(inputFileName);
		OutputStream out = new FileOutputStream(outputFileName);

		StreamUtils.copyRange(in, out, 1, 10);

		assertTrue(outputFile.exists());
		String inputFileContent = getStringFromInputStream(new FileInputStream(inputFileName));
		String outputFileContent = getStringFromInputStream(new FileInputStream(outputFileName));
		assertEquals(inputFileContent.substring(1, 11), outputFileContent);
	}

	@Test
	void whenCopyStringToOutputStream_thenCorrect() throws IOException {
		String string = "Should be copied to OutputStream.";
		String outputFileName = "src/test/resources/output.txt";
		File outputFile = new File(outputFileName);
		OutputStream out = new FileOutputStream("src/test/resources/output.txt");

		StreamUtils.copy(string, StandardCharsets.UTF_8, out);

		assertTrue(outputFile.exists());
		String outputFileContent = getStringFromInputStream(new FileInputStream(outputFileName));
		assertEquals(outputFileContent, string);
	}

	@Test
	void whenCopyInputStreamToString_thenCorrect() throws IOException {
		String inputFileName = "src/test/resources/input.txt";
		InputStream is = new FileInputStream(inputFileName);
		String content = StreamUtils.copyToString(is, StandardCharsets.UTF_8);

		String inputFileContent = getStringFromInputStream(new FileInputStream(inputFileName));
		assertEquals(inputFileContent, content);
	}

	@Test
	void whenCopyByteArrayToOutputStream_thenCorrect() throws IOException {
		String outputFileName = "src/test/resources/output.txt";
		String string = "Should be copied to OutputStream.";
		byte[] byteArray = string.getBytes();
		OutputStream out = new FileOutputStream("src/test/resources/output.txt");

		StreamUtils.copy(byteArray, out);
		String outputFileContent = getStringFromInputStream(new FileInputStream(outputFileName));
		assertEquals(outputFileContent, string);
	}

	@Test
	void whenCopyInputStreamToByteArray_thenCorrect() throws IOException {
		String inputFileName = "src/test/resources/input.txt";
		InputStream in = new FileInputStream(inputFileName);
		byte[] out = StreamUtils.copyToByteArray(in);

		String content = new String(out);
		String inputFileContent = getStringFromInputStream(new FileInputStream(inputFileName));
		assertEquals(inputFileContent, content);
	}
}