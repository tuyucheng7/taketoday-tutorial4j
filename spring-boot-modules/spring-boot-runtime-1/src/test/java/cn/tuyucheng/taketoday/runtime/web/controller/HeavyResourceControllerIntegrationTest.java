package cn.tuyucheng.taketoday.runtime.web.controller;

import cn.tuyucheng.taketoday.runtime.sampleapp.config.WebConfig;
import cn.tuyucheng.taketoday.runtime.sampleapp.web.dto.HeavyResource;
import cn.tuyucheng.taketoday.runtime.sampleapp.web.dto.HeavyResourceAddressOnly;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WebConfig.class)
@WebAppConfiguration
class HeavyResourceControllerIntegrationTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void givenHeavyResource_whenSendPutRequest_thenCreateResource() throws Exception {
		mockMvc.perform(put("/heavyresource/1")
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.content(objectMapper.writeValueAsString(new HeavyResource(1, "Tom", "Jackson", 12, "heaven street")))
		).andExpect(status().isOk());
	}

	@Test
	void givenNewAddressOfResource_whenExecutePatchRequest_thenUpdateResourcePartially() throws Exception {
		mockMvc.perform(patch("/heavyresource/1")
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.content(objectMapper.writeValueAsString(new HeavyResourceAddressOnly(1, "5th avenue")))
		).andExpect(status().isOk());
	}

	@Test
	void givenNewAddressOfResource_whenExecutePatchGeneric_thenUpdateResourcePartially() throws Exception {
		HashMap<String, Object> updates = new HashMap<>();
		updates.put("address", "5th avenue");

		mockMvc.perform(patch("/heavyresource/1")
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.content(objectMapper.writeValueAsString(updates))
		).andExpect(status().isOk());
	}
}