package cn.tuyucheng.taketoday.micronaut.vs.springboot.controller;

import cn.tuyucheng.taketoday.micronaut.vs.springboot.CompareApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = CompareApplication.class)
@AutoConfigureMockMvc
class ArithmeticControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenTwoNumbers_whenAdd_thenCorrectAnswerReturned() throws Exception {
        float expected = (float) (10 + 20);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/math/sum/10/20")
                    .accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(content().string(Float.toString(expected)));
    }

    @Test
    void givenTwoNumbers_whenSubtract_thenCorrectAnswerReturned() throws Exception {
        float expected = (float) (20 - 10);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/math/subtract/20/10")
                    .accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(content().string(Float.toString(expected)));
    }

    @Test
    void givenTwoNumbers_whenMultiply_thenCorrectAnswerReturned() throws Exception {
        float expected = (float) (20 * 10);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/math/multiply/20/10")
                    .accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(content().string(Float.toString(expected)));
    }

    @Test
    void givenTwoNumbers_whenDivide_thenCorrectAnswerReturned() throws Exception {
        float expected = (float) (20 / 10);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/math/divide/20/10")
                    .accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(content().string(Float.toString(expected)));
    }

    @Test
    void whenMemory_thenMemoryStringReturned() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/math/memory")
                    .accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(content().string(containsString("Initial:")));
    }
}