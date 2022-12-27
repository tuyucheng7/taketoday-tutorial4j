package cn.tuyucheng.taketoday.swaggerconf;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SwaggerConfExcludeErrorControllerIntegrationTest {

	@Autowired
	private MockMvc mvc;

	@Test
	void whenCallingSwaggerJSON_stringObjectDoesNotContainAnyErrorControllers() throws Exception {
		ResultActions resultActions = mvc.perform(get("/v2/api-docs")).andExpect(status().isOk());
		MvcResult result = resultActions.andReturn();
		String content = result.getResponse().getContentAsString();
		Assertions.assertNotNull(content);
		Assertions.assertFalse(content.contains("error-controller"));
	}
}