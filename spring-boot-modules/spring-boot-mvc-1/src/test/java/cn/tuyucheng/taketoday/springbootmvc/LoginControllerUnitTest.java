package cn.tuyucheng.taketoday.springbootmvc;

import cn.tuyucheng.taketoday.springbootmvc.config.CustomMessageSourceConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = LoginController.class)
@ContextConfiguration(classes = {SpringBootMvcApplication.class, CustomMessageSourceConfiguration.class})
class LoginControllerUnitTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void givenLoginForm_whenEmailFieldNotProvided_testCustomValidMessageIsReturned() throws Exception {
		String content = "{\"email\":\"\",\"password\":\"helo\"}";

		RequestBuilder builder = MockMvcRequestBuilders
			.post("/loginform")
			.contentType("application/json")
			.content(content);

		// header("accept-language", "fr").
		MvcResult perform = mockMvc.perform(builder).andReturn();
		System.out.println(perform.getResolvedException().getMessage());
		assertTrue(perform.getResolvedException().getMessage().contains("valid email"));
	}
}