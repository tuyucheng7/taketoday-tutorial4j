package cn.tuyucheng.taketoday.boot.jsoncomponent;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
@ExtendWith(SpringExtension.class)
class UserJsonDeserializerIntegrationTest {

   @Autowired
   private ObjectMapper objectMapper;

   @Test
   void testDeserialize() throws IOException {
      User user = objectMapper.readValue("{\"favoriteColor\":\"#f0f8ff\"}", User.class);
      assertEquals(Color.ALICEBLUE, user.getFavoriteColor());
   }
}