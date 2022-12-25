package cn.tuyucheng.taketoday.properties.testproperty;

import cn.tuyucheng.taketoday.properties.reloading.SpringBootPropertiesTestApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"foo=bar"}, classes = SpringBootPropertiesTestApplication.class)
class SpringBootPropertyInjectionIntegrationTest {

	@Value("${foo}")
	private String foo;

	@Test
	void whenSpringBootPropertyProvided_thenProperlyInjected() {
		assertThat(foo).isEqualTo("bar");
	}
}