package cn.tuyucheng.taketoday.web.controller;

import cn.tuyucheng.taketoday.spring.web.config.WebConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {WebConfig.class})
@WebAppConfiguration
class GreetControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private static final String CONTENT_TYPE = "application/json";

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void givenWac_whenServletContext_thenItProvidesGreetController() {
        final ServletContext servletContext = webApplicationContext.getServletContext();
        assertNotNull(servletContext);
        assertTrue(servletContext instanceof MockServletContext);
        assertNotNull(webApplicationContext.getBean("greetController"));
    }

    @Test
    void givenHomePageURI_whenMockMVC_thenReturnsIndexJSPViewName() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/homePage")).andDo(print()).andExpect(MockMvcResultMatchers.view().name("index"));
    }

    @Test
    void givenGreetURI_whenMockMVC_thenVerifyResponse() throws Exception {
        final MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/greet"))
              .andDo(print())
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Hello World!!!"))
              .andReturn();
        assertEquals(CONTENT_TYPE, mvcResult.getResponse().getContentType());
    }

    @Test
    void givenGreetURIWithPathVariable_whenMockMVC_thenResponseOK() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/greetWithPathVariable/John")).andDo(print()).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE))
              .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Hello World John!!!"));
    }

    @Test
    void givenGreetURIWithPathVariable_2_whenMockMVC_thenVerifyResponse() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/greetWithPathVariable/{name}", "Doe")).andDo(print()).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE))
              .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Hello World Doe!!!"));
    }

    @Test
    void givenGreetURIWithQueryParameter_whenMockMVC_thenResponseOK() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/greetWithQueryVariable").param("name", "John Doe")).andDo(print()).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE))
              .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Hello World John Doe!!!"));
    }

    @Test
    void givenGreetURIWithPost_whenMockMVC_thenVerifyResponse() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/greetWithPost")).andDo(print()).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE))
              .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Hello World!!!"));
    }

    @Test
    void givenGreetURIWithPostAndFormData_whenMockMVC_thenResponseOK() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/greetWithPostAndFormData").param("id", "1").param("name", "John Doe")).andDo(print()).andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE)).andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Hello World John Doe!!!")).andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }
}