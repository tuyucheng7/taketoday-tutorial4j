package cn.tuyucheng.taketoday.postman.controller;

import cn.tuyucheng.taketoday.postman.PostmanUploadDemoApplication;
import cn.tuyucheng.taketoday.postman.model.JsonRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = PostmanUploadDemoApplication.class)
@AutoConfigureMockMvc
class PostmanUploadControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenJson_whenUploaded_thenSuccessReturned() throws Exception {
        JsonRequest request = new JsonRequest(1, "John Doe");
        this.mockMvc.perform(MockMvcRequestBuilders.post("/uploadJson")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(request)))
              .andExpect(status().isOk())
              .andExpect(content().string("1John Doe"));
    }

    @Test
    void givenFile_whenUploaded_thenSuccessReturned() throws Exception {
        MockMultipartFile request = new MockMultipartFile("dummy", "{\"key\": \"value\"}".getBytes());
        this.mockMvc.perform(MockMvcRequestBuilders.multipart("/uploadFile")
                    .file("file", request.getBytes()))
              .andExpect(status().isOk())
              .andExpect(content().string("file received successfully"));
    }
}