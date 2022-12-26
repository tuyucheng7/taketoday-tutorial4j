package cn.tuyucheng.taketoday.boot.jersey.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.ws.rs.core.Response;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Import(HelloController.class)
@ExtendWith(SpringExtension.class)
class HelloControllerUnitTest {

	@Autowired
	private HelloController helloController;

	@Test
	void whenHelloIsInvokedWithCaio_thenReturn200AsStatusAndHelloCaioAsBody() {
		Response response = this.helloController.hello("Caio");
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getEntity()).isEqualTo("Hello, Caio");
	}
}