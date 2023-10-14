package cn.tuyucheng.taketoday.integrationtesting;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SecuredMethodSpringBootIntegrationTest {

	@Autowired
	private SecuredService service;

	@Test
	void givenUnauthenticated_whenCallService_thenThrowsException() {
		assertThrows(AuthenticationCredentialsNotFoundException.class, () -> service.sayHelloSecured());
	}

	@WithMockUser(username = "spring")
	@Test
	void givenAuthenticated_whenCallServiceWithSecured_thenOk() {
		assertThat(service.sayHelloSecured()).isNotBlank();
	}
}