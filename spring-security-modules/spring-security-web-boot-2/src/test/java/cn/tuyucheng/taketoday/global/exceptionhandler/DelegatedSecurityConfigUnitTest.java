package cn.tuyucheng.taketoday.global.exceptionhandler;

import cn.tuyucheng.taketoday.global.exceptionhandler.controller.LoginController;
import cn.tuyucheng.taketoday.global.exceptionhandler.handler.RestError;
import cn.tuyucheng.taketoday.global.exceptionhandler.security.CustomAuthenticationEntryPoint;
import cn.tuyucheng.taketoday.global.exceptionhandler.security.DelegatedAuthenticationEntryPoint;
import cn.tuyucheng.taketoday.global.exceptionhandler.security.DelegatedSecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(DelegatedSecurityConfig.class)
@Import({LoginController.class, CustomAuthenticationEntryPoint.class, DelegatedAuthenticationEntryPoint.class})
class DelegatedSecurityConfigUnitTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenUserAccessLogin_shouldSucceed() throws Exception {
        mvc.perform(formLogin("/login-handler").user("username", "admin")
                    .password("password", "password")
                    .acceptMediaType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk());
    }

    @Test
    void whenUserAccessWithWrongCredentialsWithDelegatedEntryPoint_shouldFail() throws Exception {
        RestError re = new RestError(HttpStatus.UNAUTHORIZED.toString(), "Authentication failed at controller advice");
        mvc.perform(formLogin("/login-handler").user("username", "admin")
                    .password("password", "wrong")
                    .acceptMediaType(MediaType.APPLICATION_JSON))
              .andExpect(status().isUnauthorized())
              .andExpect(jsonPath("$.errorMessage", is(re.getErrorMessage())));
    }
}