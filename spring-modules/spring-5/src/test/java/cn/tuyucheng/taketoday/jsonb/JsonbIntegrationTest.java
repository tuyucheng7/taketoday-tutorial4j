package cn.tuyucheng.taketoday.jsonb;

import cn.tuyucheng.taketoday.jupiter.SpringExtension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Spring5Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Disabled
class JsonbIntegrationTest {

	@Autowired
	private TestRestTemplate template;

	@Test
	void givenId_whenUriIsPerson_thenGetPerson() {
		ResponseEntity<Person> response = template
			.getForEntity("/person/1", Person.class);
		Person person = response.getBody();
		assertEquals(person, new Person(2, "Jhon", "jhon1@test.com", 0, LocalDate.of(2019, 9, 9), BigDecimal.valueOf(1500.0)));
	}

	@Test
	void whenSendPostAPerson_thenGetOkStatus() {
		ResponseEntity<Boolean> response = template.withBasicAuth("user", "password").
			postForEntity("/person", "{\"birthDate\":\"07-09-2017\",\"email\":\"jhon1@test.com\",\"person-name\":\"Jhon\",\"id\":10}", Boolean.class);
		assertEquals(TRUE, response.getBody());
	}
}