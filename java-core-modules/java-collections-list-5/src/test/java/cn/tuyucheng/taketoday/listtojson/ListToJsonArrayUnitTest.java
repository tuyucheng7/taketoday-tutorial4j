package cn.tuyucheng.taketoday.listtojson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class ListToJsonArrayUnitTest {
   public List<String> list = Arrays.asList("Article 1", "Article 2", "Article 3");
   public String expectedJsonArray = "[\"Article 1\",\"Article 2\",\"Article 3\"]";

   @Test
   public void given_JavaList_whenUsingJacksonLibrary_thenOutJsonArray() throws JsonProcessingException {
      ObjectMapper objectMapper = new ObjectMapper();
      String jsonArray = objectMapper.writeValueAsString(list);
      Assertions.assertEquals(expectedJsonArray, jsonArray);
   }

   @Test
   public void given_JavaList_whenUsingGsonLibrary_thenOutJsonArray() {
      Gson gson = new Gson();
      String jsonArray = gson.toJson(list);
      Assertions.assertEquals(expectedJsonArray, jsonArray);
   }

   @Test
   public void given_JavaList_whenOrgJsonLibrary_thenOutJsonAray() {
      JSONArray jsonArray = new JSONArray(list);
      Assertions.assertEquals(expectedJsonArray, jsonArray.toString());
   }

}
