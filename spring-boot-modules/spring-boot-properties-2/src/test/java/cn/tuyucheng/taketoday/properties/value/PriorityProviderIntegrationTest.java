package cn.tuyucheng.taketoday.properties.value;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PriorityProvider.class)
class PriorityProviderIntegrationTest {

	@Autowired
	private PriorityProvider priorityProvider;

	@Test
	void givenPropertyFileWhenConstructorInjectionUsedThenValueInjected() {
		assertThat(priorityProvider.getPriority()).isEqualTo("high");
	}
}