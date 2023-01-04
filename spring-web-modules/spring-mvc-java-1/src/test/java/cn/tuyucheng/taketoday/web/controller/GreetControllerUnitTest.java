package cn.tuyucheng.taketoday.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class GreetControllerUnitTest {

    private MockMvc mockMvc;
    private static final String CONTENT_TYPE = "application/json";

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new GreetController()).build();
    }

    @Test
    void givenHomePageURI_whenMockMVC_thenReturnsIndexJSPViewName() throws Exception {
        this.mockMvc.perform(get("/homePage")).andExpect(view().name("index"));
    }

    @Test
    void givenGreetURI_whenMockMVC_thenVerifyResponse() throws Exception {
        this.mockMvc.perform(get("/greet")).andExpect(status().isOk()).andExpect(content().contentType(CONTENT_TYPE)).andExpect(jsonPath("$.message").value("Hello World!!!"));
    }

    @Test
    void givenGreetURIWithPathVariable_whenMockMVC_thenVerifyResponse() throws Exception {
        this.mockMvc.perform(get("/greetWithPathVariable/John")).andExpect(status().isOk()).andExpect(content().contentType(CONTENT_TYPE)).andExpect(jsonPath("$.message").value("Hello World John!!!"));
    }

    @Test
    void givenGreetURIWithPathVariable_2_whenMockMVC_thenVerifyResponse() throws Exception {
        this.mockMvc.perform(get("/greetWithPathVariable/{name}", "Doe")).andExpect(status().isOk()).andExpect(content().contentType(CONTENT_TYPE)).andExpect(jsonPath("$.message").value("Hello World Doe!!!"));
    }

    @Test
    void givenGreetURIWithQueryParameter_whenMockMVC_thenVerifyResponse() throws Exception {
        this.mockMvc.perform(get("/greetWithQueryVariable").param("name", "John Doe")).andDo(print()).andExpect(status().isOk()).andExpect(content().contentType(CONTENT_TYPE)).andExpect(jsonPath("$.message").value("Hello World John Doe!!!"));
    }

    @Test
    void givenGreetURIWithPost_whenMockMVC_thenVerifyResponse() throws Exception {
        this.mockMvc.perform(post("/greetWithPost")).andDo(print()).andExpect(status().isOk()).andExpect(content().contentType(CONTENT_TYPE)).andExpect(jsonPath("$.message").value("Hello World!!!"));
    }

    @Test
    void givenGreetURIWithPostAndFormData_whenMockMVC_thenVerifyResponse() throws Exception {
        this.mockMvc.perform(post("/greetWithPostAndFormData").param("id", "1").param("name", "John Doe")).andDo(print()).andExpect(status().isOk()).andExpect(content().contentType(CONTENT_TYPE))
              .andExpect(jsonPath("$.message").value("Hello World John Doe!!!")).andExpect(jsonPath("$.id").value(1));
    }
}