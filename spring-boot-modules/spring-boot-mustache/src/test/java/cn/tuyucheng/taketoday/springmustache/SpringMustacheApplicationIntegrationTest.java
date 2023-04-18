package cn.tuyucheng.taketoday.springmustache;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SpringMustacheApplicationIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void givenIndexPageWhenContainsArticleThenTrue() {
		ResponseEntity<String> entity = this.restTemplate.getForEntity("/article", String.class);

		assertEquals(entity.getStatusCode(), HttpStatus.OK);
		assertTrue(Objects.requireNonNull(entity.getBody()).contains("Article Title 0"));
	}
}