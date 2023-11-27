package cn.tuyucheng.taketoday.boot.jsoncomponent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
@ExtendWith(SpringExtension.class)
class UserJsonSerializerIntegrationTest {

   @Autowired
   private ObjectMapper objectMapper;

   @Test
   void testSerialization() throws JsonProcessingException {
      String json = objectMapper.writeValueAsString(new User(Color.ALICEBLUE));
      assertEquals("{\"favoriteColor\":\"#f0f8ff\"}", json);
   }
}