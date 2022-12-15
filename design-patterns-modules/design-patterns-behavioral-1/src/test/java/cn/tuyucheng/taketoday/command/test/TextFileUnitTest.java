package cn.tuyucheng.taketoday.command.test;

import cn.tuyucheng.taketoday.command.receiver.TextFile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TextFileUnitTest {

	private static TextFile textFile;

	@BeforeAll
	static void setUpTextFileInstance() {
		textFile = new TextFile("file1.txt");
	}

	@Test
	void givenTextFileInstance_whenCalledopenMethod_thenOneAssertion() {
		assertThat(textFile.open()).isEqualTo("Opening file file1.txt");
	}

	@Test
	void givenTextFileInstance_whenCalledwriteMethod_thenOneAssertion() {
		assertThat(textFile.write()).isEqualTo("Writing to file file1.txt");
	}

	@Test
	void givenTextFileInstance_whenCalledsaveMethod_thenOneAssertion() {
		assertThat(textFile.save()).isEqualTo("Saving file file1.txt");
	}

	@Test
	void givenTextFileInstance_whenCalledcopyMethod_thenOneAssertion() {
		assertThat(textFile.copy()).isEqualTo("Copying file file1.txt");
	}

	@Test
	void givenTextFileInstance_whenCalledpasteMethod_thenOneAssertion() {
		assertThat(textFile.paste()).isEqualTo("Pasting file file1.txt");
	}
}