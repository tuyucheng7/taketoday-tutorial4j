package cn.tuyucheng.taketoday.boot.mvc.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Import(HelloController.class)
@ExtendWith(SpringExtension.class)
class HelloControllerUnitTest {

	@Autowired
	private HelloController helloController;

	@Test
	void whenHelloIsInvokedWithCaio_thenReturn200AsStatusAndHelloCaioAsBody() {
		ResponseEntity response = this.helloController.hello("Caio");
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo("Hello, Caio");
	}
}