package cn.tuyucheng.taketoday.buildproperties;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertThat;

@SpringBootTest(classes = Application.class)
class BuildInfoServiceIntegrationTest {

	@Autowired
	private BuildInfoService service;

	@Test
	void whenGetApplicationDescription_thenSuccess() {
		assertThat(service.getApplicationDescription(), Matchers.is("Spring Boot Properties Module"));
		assertThat(service.getApplicationVersion(), Matchers.is("1.0.0"));
	}
}