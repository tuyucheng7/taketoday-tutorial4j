package cn.tuyucheng.taketoday.problemdetails.web;

import cn.tuyucheng.taketoday.problemdetails.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class EmployeeControllerIntegrationTest {

	@LocalServerPort
	private int port;

	private WebClient webClient;
	private RestTemplate restTemplate;

	@BeforeEach
	void setUp() {
		String baseUrl = "http://localhost:" + this.port;
		this.webClient = WebClient.builder().baseUrl(baseUrl).build();
		this.restTemplate = new RestTemplate();
		this.restTemplate.setUriTemplateHandler(new RootUriTemplateHandler(baseUrl));
	}

	@Test
	public void testAddEmployee_V1_FailsWhen_IncorrectId() {
		try {
			this.restTemplate.getForObject("/employees/v1/101", Employee.class);
		} catch (RestClientResponseException ex) {
			ProblemDetail pd = ex.getResponseBodyAs(ProblemDetail.class);
			System.out.println(format(ex.getStatusCode(), ex.getResponseHeaders(), pd));
			assertEquals(404, pd.getStatus());
		}
	}

	@Test
	public void testAddEmployee_V2_FailsWhen_IncorrectId() {
		try {
			this.restTemplate.getForObject("/employees/v2/101", Employee.class);
		} catch (RestClientResponseException ex) {
			ProblemDetail pd = ex.getResponseBodyAs(ProblemDetail.class);
			System.out.println(format(ex.getStatusCode(), ex.getResponseHeaders(), pd));
			assertEquals("Employee id '101' does no exist", pd.getDetail());
			assertEquals(404, pd.getStatus());
			assertEquals("Record Not Found", pd.getTitle());
			assertEquals(URI.create("http://my-app-host.com/errors/not-found"), pd.getType());
			assertEquals("localhost", pd.getProperties().get("hostname"));
		}
	}

	@Test
	void testAddEmployeeUsingWebFlux_V2_FailsWhen_IncorrectId() {
		this.webClient.get().uri("/employees/v2/101")
			.retrieve()
			.bodyToMono(String.class)
			.doOnNext(System.out::println)
			.onErrorResume(WebClientResponseException.class, ex -> {
				ProblemDetail pd = ex.getResponseBodyAs(ProblemDetail.class);
				assertEquals("Employee id '101' does no exist", pd.getDetail());
				assertEquals(404, pd.getStatus());
				assertEquals("Record Not Found", pd.getTitle());
				assertEquals(URI.create("http://my-app-host.com/errors/not-found"), pd.getType());
				assertEquals("localhost", pd.getProperties().get("hostname"));
				System.out.println(format(ex.getStatusCode(), ex.getHeaders(), pd));
				return Mono.empty();
			})
			.block();
	}

	private String format(HttpStatusCode statusCode, @Nullable HttpHeaders headers,
						  ProblemDetail body) {
		return "\nHTTP Status " + statusCode + "\n\n" +
			(headers != null ? headers : HttpHeaders.EMPTY).entrySet().stream()
				.map(entry -> entry.getKey() + ":" + entry.getValue())
				.collect(Collectors.joining("\n")) +
			"\n\n" + body;
	}
}