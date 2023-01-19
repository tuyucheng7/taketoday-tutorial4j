package cn.tuyucheng.taketoday.configurationproperties

import cn.tuyucheng.taketoday.configurationproperties.config.ApiConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
class SpringBootV2ApplicationIntegrationTest {

	@Autowired
	lateinit var apiConfiguration: ApiConfiguration

	@Test
	fun givenExternalConfigProps_whenUsedConstructorBinding_thenBindExternalProperties() {
		assertThat(apiConfiguration).isNotNull
		assertThat(apiConfiguration.url).isNotBlank
		assertThat(apiConfiguration.clientId).isNotBlank
		assertThat(apiConfiguration.key).isNotBlank
	}
}