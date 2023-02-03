package cn.tuyucheng.taketoday.newfeatures;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TextBlocksUnitTest {

	private static final String JSON_STRING = "{\r\n" + "\"name\" : \"Tuyucheng\",\r\n" + "\"website\" : \"https://www.%s.com/\"\r\n" + "}";

	@SuppressWarnings("preview")
	private static final String TEXT_BLOCK_JSON = """
		    {
		    "name" : "Tuyucheng",
		    "website" : "https://www.%s.com/"
		    }
		""";

	@Test
	void whenTextBlocks_thenStringOperationsWork() {
		assertThat(TEXT_BLOCK_JSON).contains("Tuyucheng");
		assertThat(TEXT_BLOCK_JSON.indexOf("www")).isPositive();
		assertThat(TEXT_BLOCK_JSON).isNotEmpty();
	}

	@SuppressWarnings("removal")
	@Test
	void whenTextBlocks_thenFormattedWorksAsFormat() {
		assertThat(TEXT_BLOCK_JSON.formatted("tuyucheng")).contains("www.tuyucheng.com");

		assertThat(String.format(JSON_STRING, "tuyucheng")).contains("www.tuyucheng.com");
	}
}