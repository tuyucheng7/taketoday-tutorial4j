package cn.tuyucheng.taketoday.antmatchers.controllers;

import cn.tuyucheng.taketoday.antmatchers.AntMatchersExampleApplication;
import cn.tuyucheng.taketoday.antmatchers.config.SecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = ProductController.class)
@ContextConfiguration(classes = {AntMatchersExampleApplication.class, SecurityConfiguration.class})
class ProductControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void getProducts() throws Exception {
		mockMvc.perform(get("/products"))
			.andExpect(status().isOk());
	}
}