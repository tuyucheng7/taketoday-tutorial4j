package cn.tuyucheng.taketoday.properties.testproperty;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {"foo=bar"})
class PropertyInjectionUnitTest {

	@Value("${foo}")
	private String foo;

	@Test
	void whenPropertyProvided_thenProperlyInjected() {
		assertThat(foo).isEqualTo("bar");
	}
}