package cn.tuyucheng.taketoday.configurationproperties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ImmutableConfigPropertiesApplication.class)
@TestPropertySource("classpath:configprops-test.properties")
class ImmutableConfigurationPropertiesIntegrationTest {

	@Autowired
	private ImmutableCredentials immutableCredentials;

	@Test
	void whenConstructorBindingUsed_thenPropertiesCorrectlyBound() {
		assertThat(immutableCredentials.getAuthMethod()).isEqualTo("SHA1");
		assertThat(immutableCredentials.getUsername()).isEqualTo("john");
		assertThat(immutableCredentials.getPassword()).isEqualTo("password");
	}
}