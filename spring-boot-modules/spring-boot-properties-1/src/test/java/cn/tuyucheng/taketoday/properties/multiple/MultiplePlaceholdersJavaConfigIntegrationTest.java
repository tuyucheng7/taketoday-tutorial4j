package cn.tuyucheng.taketoday.properties.multiple;

import cn.tuyucheng.taketoday.properties.spring.PropertySourcesPlaceholderConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig({PropertySourcesPlaceholderConfig.class})
class MultiplePlaceholdersJavaConfigIntegrationTest {

	@Value("${key.something}")
	private String something;


	@Test
	void whenReadInjectedValues_thenGetCorrectValues() {
		assertThat(something).isEqualTo("val");
	}
}