package cn.tuyucheng.taketoday.properties.multidocument;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {MultidocumentTestConfig.class})
class MultidocumentPropertiesFileWithDevProfileIntegrationTest {

	Logger logger = LoggerFactory.getLogger(MultidocumentPropertiesFileWithDevProfileIntegrationTest.class);

	@Value("${tuyucheng.customProperty}")
	private String customProperty;

	@Test
	void givenMultidocumentPropertiesFileWhenBootContextLoadedThenDocumentProcessedCorrectly() {
		assertThat(customProperty).isEqualTo("valueTest");
	}
}