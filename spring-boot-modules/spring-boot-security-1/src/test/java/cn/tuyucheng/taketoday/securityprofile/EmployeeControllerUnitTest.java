package cn.tuyucheng.taketoday.securityprofile;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = EmployeeController.class)
@ActiveProfiles("prod")
@ContextConfiguration(classes = {Application.class, ApplicationSecurity.class})
class EmployeeControllerUnitTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void whenSecurityEnabled_shouldBeForbidden() throws Exception {
		this.mockMvc.perform(get("/employees"))
			.andExpect(status().isForbidden());
	}
}