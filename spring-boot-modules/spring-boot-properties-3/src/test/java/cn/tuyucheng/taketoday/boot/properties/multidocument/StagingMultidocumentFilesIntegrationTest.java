package cn.tuyucheng.taketoday.boot.properties.multidocument;

import cn.tuyucheng.taketoday.boot.properties.DemoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {DemoApplication.class}, webEnvironment = WebEnvironment.MOCK)
@ActiveProfiles("multidocument-staging")
class StagingMultidocumentFilesIntegrationTest {

	@Value("${tuyucheng.property}")
	private String tuyuchengCustomProperty;

	@Value("${tuyucheng.stagingProperty}")
	private String tuyuchengStagingProperty;

	@Value("${tuyucheng.root-level-property}")
	private String tuyuchengRootProperty;

	@Test
	void givenProductionProfileActive_whenApplicationStarts_thenDefaultPropertiesUser() {
		assertThat(tuyuchengStagingProperty).isEqualTo("stagingPropertyValue");
		// application.properties is loaded after the application.yml file and overrides the values
		assertThat(tuyuchengCustomProperty).isEqualTo("defaultValue");
		assertThat(tuyuchengRootProperty).isEqualTo("defaultRootLevelValue");
	}
}