package cn.tuyucheng.taketoday.entitydtodifferences.controller;

import cn.tuyucheng.taketoday.entitydtodifferences.entity.Book;
import cn.tuyucheng.taketoday.entitydtodifferences.entity.User;
import cn.tuyucheng.taketoday.entitydtodifferences.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private UserRepository userRepository;

   @Autowired
   private ObjectMapper objectMapper;

   @BeforeEach
   void beforeEach() {
      userRepository.deleteAll();
   }

   @Test
   void givenUsersAreExisting_whenGetUsers_thenUsersAreReturned() throws Exception {
      // given
      Book book1 = new Book("Book1", "Author1");
      Book book2 = new Book("Book2", "Author2");
      User savedUser = userRepository.save(new User("John", "Doe", "123 Main St", Arrays.asList(book1, book2)));
      String expectedJson = String.format(
            "[{\"ID\":%s,\"FIRST_NAME\":\"John\",\"LAST_NAME\":\"Doe\",\"BOOKS\":[{\"NAME\":\"Book1\",\"AUTHOR\":\"Author1\"},{\"NAME\":\"Book2\",\"AUTHOR\":\"Author2\"}]}]",
            savedUser.getId());
      // when
      MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users")
                  .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
      // then
      assertEquals(expectedJson, result.getResponse()
            .getContentAsString());
   }

   @Test
   void givenValidUser_whenPostUser_thenUserIsCreated() throws Exception {
      // given
      String user = "{\"FIRST_NAME\":\"John\",\"LAST_NAME\":\"Doe\",\"BOOKS\":[{\"NAME\":\"Book1\",\"AUTHOR\":\"Author1\"},{\"NAME\":\"Book2\",\"AUTHOR\":\"Author2\"}]}";
      // when
      MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                  .content(user)
                  .contentType(MediaType.APPLICATION_JSON)
                  .accept(MediaType.APPLICATION_JSON))
            .andReturn();
      JsonNode responseJson = objectMapper.readTree(result.getResponse()
            .getContentAsString());
      // then
      assertEquals("John", responseJson.get("FIRST_NAME")
            .asText());
      assertEquals("Doe", responseJson.get("LAST_NAME")
            .asText());
      assertTrue(responseJson.has("BOOKS"));
      JsonNode booksArray = responseJson.get("BOOKS");
      assertEquals(2, booksArray.size());
      assertEquals("Book1", booksArray.get(0)
            .get("NAME")
            .asText());
      assertEquals("Author1", booksArray.get(0)
            .get("AUTHOR")
            .asText());
      assertEquals("Book2", booksArray.get(1)
            .get("NAME")
            .asText());
      assertEquals("Author2", booksArray.get(1)
            .get("AUTHOR")
            .asText());
   }
}