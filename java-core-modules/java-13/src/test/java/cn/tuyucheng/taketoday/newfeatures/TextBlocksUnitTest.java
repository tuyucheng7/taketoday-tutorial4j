package cn.tuyucheng.taketoday.newfeatures;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TextBlocksUnitTest {

   private static final String JSON_STRING = "{\r\n" + "\"name\" : \"Tuyucheng\",\r\n" + "\"website\" : \"https://www.%s.com/\"\r\n" + "}";

   private static final String TEXT_BLOCK_JSON = """
             {
             "name" : "Tuyucheng",
             "website" : "https://www.%s.com/"
             }
         """;

   @Test
   public void whenTextBlocks_thenStringOperationsWork() {

      assertThat(TEXT_BLOCK_JSON.contains("Tuyucheng")).isTrue();
      assertThat(TEXT_BLOCK_JSON.indexOf("www")).isGreaterThan(0);
      assertThat(TEXT_BLOCK_JSON.length()).isGreaterThan(0);

   }

   @Test
   public void whenTextBlocks_thenFormattedWorksAsFormat() {
      assertThat(TEXT_BLOCK_JSON.formatted("tuyucheng")
            .contains("www.tuyucheng.com")).isTrue();

      assertThat(String.format(JSON_STRING, "tuyucheng")
            .contains("www.tuyucheng.com")).isTrue();

   }

}
