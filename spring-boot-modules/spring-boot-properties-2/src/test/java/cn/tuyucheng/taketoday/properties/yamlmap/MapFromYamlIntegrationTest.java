package cn.tuyucheng.taketoday.properties.yamlmap;

import cn.tuyucheng.taketoday.properties.yamlmap.pojo.ServerProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class MapFromYamlIntegrationTest {

	@Autowired
	private ServerProperties serverProperties;

	@Test
	void whenYamlFileProvidedThenInjectSimpleMap() {
		assertThat(serverProperties.getApplication()).containsOnlyKeys("name", "url", "description");

		assertThat(serverProperties.getApplication())
			.containsEntry("name", "InjectMapFromYAML");
	}

	@Test
	void whenYamlFileProvidedThenInjectComplexMap() {
		assertThat(serverProperties.getConfig()).hasSize(2);

		assertThat(serverProperties.getConfig()
			.get("ips")
			.get(0)).isEqualTo("10.10.10.10");

		assertThat(serverProperties.getUsers()
			.get("root")
			.getUsername()).isEqualTo("root");
	}
}