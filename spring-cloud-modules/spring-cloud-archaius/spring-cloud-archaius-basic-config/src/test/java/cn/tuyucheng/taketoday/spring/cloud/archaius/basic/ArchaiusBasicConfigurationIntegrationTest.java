package cn.tuyucheng.taketoday.spring.cloud.archaius.basic;

import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ArchaiusBasicConfigurationIntegrationTest {

	@Autowired
	ConfigurableApplicationContext context;

	private DynamicStringProperty testPropertyWithDynamic = DynamicPropertyFactory.getInstance()
		.getStringProperty("tuyucheng.archaius.test.properties.one", "not found!");

	@Test
	void givenInitialPropertyValue_whenPropertyChanges_thenArchaiusRetrievesNewValue() {
		String initialValue = testPropertyWithDynamic.get();

		TestPropertyValues.of("tuyucheng.archaius.test.properties.one=new-value")
			.applyTo(context);
		context.publishEvent(new EnvironmentChangeEvent(Collections.singleton("tuyucheng.archaius.test.properties.one")));
		String finalValue = testPropertyWithDynamic.get();

		assertThat(initialValue).isEqualTo("test-one");
		assertThat(finalValue).isEqualTo("new-value");
	}
}