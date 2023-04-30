package cn.tuyucheng.taketoday.gcp.firebase.publisher.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class FirebasePublisherControllerLiveTest {

	@LocalServerPort
	int serverPort;

	@Autowired
	TestRestTemplate restTemplate;

	@Test
	void testWhenPostTopicMessage_thenSuccess() throws Exception {
		URI uri = new URI("http://localhost:" + serverPort + "/topics/my-topic");
		ResponseEntity<String> response = restTemplate.postForEntity(uri, "Hello, world", String.class);

		assertNotNull(response);
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		assertNotNull(response.getBody());
	}

	@Test
	void testWhenPostClientMessage_thenSuccess() throws Exception {
		URI uri = new URI("http://localhost:" + serverPort + "/clients/fake-registration1");
		ResponseEntity<String> response = restTemplate.postForEntity(uri, "Hello, world", String.class);

		assertNotNull(response);
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		assertNotNull(response.getBody());
	}
}