package cn.tuyucheng.taketoday.reactive.webclient;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@RestController
public class WebController {

	private static final int DEFAULT_PORT = 8080;

	@Setter
	private int serverPort = DEFAULT_PORT;

	@GetMapping("/tweets-blocking")
	public List<Tweet> getTweetsBlocking() {
		LOGGER.info("Starting BLOCKING Controller!");
		final String uri = getSlowServiceUri();

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<List<Tweet>> response = restTemplate.exchange(
			uri, HttpMethod.GET, null,
			new ParameterizedTypeReference<>() {
			});

		List<Tweet> result = response.getBody();
		result.forEach(tweet -> LOGGER.info(tweet.toString()));
		LOGGER.info("Exiting BLOCKING Controller!");
		return result;
	}

	@GetMapping(value = "/tweets-non-blocking", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Tweet> getTweetsNonBlocking() {
		LOGGER.info("Starting NON-BLOCKING Controller!");
		Flux<Tweet> tweetFlux = WebClient.create()
			.get()
			.uri(getSlowServiceUri())
			.retrieve()
			.bodyToFlux(Tweet.class);

		tweetFlux.subscribe(tweet -> LOGGER.info(tweet.toString()));
		LOGGER.info("Exiting NON-BLOCKING Controller!");
		return tweetFlux;
	}

	private String getSlowServiceUri() {
		return "http://localhost:" + serverPort + "/slow-service-tweets";
	}
}