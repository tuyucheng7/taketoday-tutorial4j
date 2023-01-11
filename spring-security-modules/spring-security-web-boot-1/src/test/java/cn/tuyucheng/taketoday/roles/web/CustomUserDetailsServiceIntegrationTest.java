package cn.tuyucheng.taketoday.roles.web;

import cn.tuyucheng.taketoday.roles.custom.CustomSecurityExpressionApplication;
import cn.tuyucheng.taketoday.roles.custom.persistence.model.Foo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {CustomSecurityExpressionApplication.class})
@AutoConfigureMockMvc
class CustomUserDetailsServiceIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @WithUserDetails("john")
    void givenUserWithReadPermissions_whenRequestUserInfo_thenRetrieveUserData() throws Exception {
        this.mvc.perform(get("/user").with(csrf()))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.user.privileges[0].name").value("FOO_READ_PRIVILEGE"))
              .andExpect(jsonPath("$.user.organization.name").value("FirstOrg"))
              .andExpect(jsonPath("$.user.username").value("john"));
    }

    @Test
    @WithUserDetails("tom")
    void givenUserWithWritePermissions_whenRequestUserInfo_thenRetrieveUserData() throws Exception {
        this.mvc.perform(get("/user").with(csrf()))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.user.privileges").isArray())
              .andExpect(jsonPath("$.user.organization.name").value("SecondOrg"))
              .andExpect(jsonPath("$.user.username").value("tom"));
    }

    @Test
    @WithUserDetails("john")
    void givenUserWithReadPermissions_whenRequestFoo_thenRetrieveSampleFoo() throws Exception {
        this.mvc.perform(get("/foos/1").with(csrf()))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.name").value("Sample"));
    }

    @Test
    @WithAnonymousUser
    void givenAnonymous_whenRequestFoo_thenRetrieveUnauthorized() throws Exception {
        this.mvc.perform(get("/foos/1").with(csrf()))
              .andExpect(status().isFound());
    }

    @Test
    @WithUserDetails("john")
    void givenUserWithReadPermissions_whenCreateNewFoo_thenForbiddenStatusRetrieved() throws Exception {
        this.mvc.perform(post("/foos").with(csrf())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .content(asJsonString(new Foo())))
              .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("tom")
    void givenUserWithWritePermissions_whenCreateNewFoo_thenOkStatusRetrieved() throws Exception {
        this.mvc.perform(post("/foos").with(csrf())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .content(asJsonString(new Foo())))
              .andExpect(status().isCreated());
    }

    private static String asJsonString(final Object obj) throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }
}