package cn.tuyucheng.taketoday.properties.additionalproperties;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("default")
@SpringBootTest(classes = {AdditionalPropertiesTestConfig.class})
class AdditionalPropertiesFilesIntegrationTest {

	Logger logger = LoggerFactory.getLogger(AdditionalPropertiesFilesIntegrationTest.class);

	@Value("${tuyucheng.additionalProperty}")
	private String additionalProperty;

	@Value("${tuyucheng.otherProperty}")
	private String otherProperty;

	@Test
	void givenMultidocumentPropertiesFileWhenBootContextLoadedThenDocumentProcessedCorrectly() {
		assertThat(additionalProperty).isEqualTo("additionalValue2");
		assertThat(otherProperty).isEqualTo("latterValue");
	}
}