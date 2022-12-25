package cn.tuyucheng.taketoday.properties.value;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CollectionProvider.class)
class CollectionProviderIntegrationTest {

	@Autowired
	private CollectionProvider collectionProvider;

	@Test
	void givenPropertyFileWhenSetterInjectionUsedThenValueInjected() {
		assertThat(collectionProvider.getValues()).contains("A", "B", "C");
	}
}