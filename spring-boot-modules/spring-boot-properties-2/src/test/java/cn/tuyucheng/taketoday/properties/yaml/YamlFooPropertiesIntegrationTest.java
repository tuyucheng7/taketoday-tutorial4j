package cn.tuyucheng.taketoday.properties.yaml;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class YamlFooPropertiesIntegrationTest {

	@Autowired
	private YamlFooProperties yamlFooProperties;

	@Test
	void whenFactoryProvidedThenYamlPropertiesInjected() {
		assertThat(yamlFooProperties.getName()).isEqualTo("foo");
		assertThat(yamlFooProperties.getAliases()).containsExactly("abc", "xyz");
	}
}