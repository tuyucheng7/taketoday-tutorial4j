package cn.tuyucheng.taketoday.batchinserts;

import cn.tuyucheng.taketoday.boot.Application;
import cn.tuyucheng.taketoday.boot.daos.CustomerRepository;
import cn.tuyucheng.taketoday.boot.web.controllers.CustomerController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("stats")
class BatchInsertIntegrationTest {

	@Autowired
	private CustomerRepository customerRepository;
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(new CustomerController(customerRepository))
			.build();
	}

	@Test
	void whenInsertingCustomers_thenCustomersAreCreated() throws Exception {
		this.mockMvc.perform(post("/customers"))
			.andExpect(status().isOk());

	}
}