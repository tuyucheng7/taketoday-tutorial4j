package cn.tuyucheng.taketoday.jsonpath.introduction;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonPathUnitTest {

   private static String json;
   private static File jsonFile = new File("src/main/resources/online_store.json");

   private static String readFile(File file, Charset charset) throws IOException {
      return Files.readString(file.toPath(), charset);
   }

   @BeforeAll
   static void init() throws IOException {
      json = readFile(jsonFile, StandardCharsets.UTF_8);
   }

   @Test
   void shouldMatchCountOfObjects() {
      Map<String, String> objectMap = JsonPath.read(json, "$");
      assertEquals(3, objectMap.keySet()
            .size());
   }

   @Test
   void shouldMatchCountOfArrays() {
      JSONArray jsonArray = JsonPath.read(json, "$.items.book[*]");
      assertEquals(2, jsonArray.size());
   }
}