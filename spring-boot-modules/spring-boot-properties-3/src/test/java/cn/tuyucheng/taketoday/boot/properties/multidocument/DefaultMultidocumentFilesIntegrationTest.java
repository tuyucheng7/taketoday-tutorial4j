package cn.tuyucheng.taketoday.boot.properties.multidocument;

import cn.tuyucheng.taketoday.boot.properties.DemoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {DemoApplication.class}, webEnvironment = WebEnvironment.MOCK)
class DefaultMultidocumentFilesIntegrationTest {

	@Value("${tuyucheng.property}")
	private String tuyuchengCustomProperty;

	@Value("${tuyucheng.root-level-property}")
	private String tuyuchengRootProperty;

	@Test
	void givenDefaultProfileActive_whenApplicationStarts_thenDefaultPropertiesUser() {
		assertThat(tuyuchengCustomProperty).isEqualTo("defaultValue");
		assertThat(tuyuchengRootProperty).isEqualTo("defaultRootLevelValue");
	}
}