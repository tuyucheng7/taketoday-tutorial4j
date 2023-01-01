package cn.tuyucheng.taketoday.annotation.servletcomponentscan;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringBootPlainApp.class)
class SpringBootWithoutServletComponentIntegrationTest {

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void givenServletContext_whenAccessAttrs_thenNotFound() {
		assertNull(servletContext.getAttribute("servlet-context-attr"));
	}

	@Test
	void givenServletFilter_whenGetHello_thenEndpointNotFound() {
		ResponseEntity<String> responseEntity = this.restTemplate.getForEntity("/hello", String.class);
		assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
	}

	@Test
	void givenServletContext_whenCheckFilterMappings_thenEmpty() {
		assertNotNull(servletContext);
		FilterRegistration filterRegistration = servletContext.getFilterRegistration("hello filter");

		assertNull(filterRegistration);
	}
}