package cn.tuyucheng.taketoday.buildproperties;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BuildInfoApplication.class)
class BuildInfoServiceIntegrationTest {

	@Autowired
	private BuildInfoService service;

	@Test
	void whenGetApplicationDescription_thenSuccess() {
		assertThat(service.getApplicationDescription(), Matchers.is("Spring Boot Properties Module"));
		assertThat(service.getApplicationVersion(), Matchers.is("1.0.0"));
	}
}