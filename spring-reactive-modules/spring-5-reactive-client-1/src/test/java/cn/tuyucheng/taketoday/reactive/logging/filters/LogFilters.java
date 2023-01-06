package cn.tuyucheng.taketoday.reactive.logging.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class LogFilters {
	public static List<ExchangeFilterFunction> prepareFilters() {
		return Arrays.asList(logRequest(), logResponse());
	}

	private static ExchangeFilterFunction logRequest() {
		return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
			if (LOGGER.isDebugEnabled()) {
				StringBuilder sb = new StringBuilder("Request: \n")
					.append(clientRequest.method())
					.append(" ")
					.append(clientRequest.url());
				clientRequest
					.headers()
					.forEach((name, values) -> values.forEach(value -> sb
						.append("\n")
						.append(name)
						.append(":")
						.append(value)));
				LOGGER.debug(sb.toString());
			}
			return Mono.just(clientRequest);
		});
	}

	private static ExchangeFilterFunction logResponse() {
		return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
			if (LOGGER.isDebugEnabled()) {
				StringBuilder sb = new StringBuilder("Response: \n")
					.append("Status: ")
					.append(clientResponse.rawStatusCode());
				clientResponse
					.headers()
					.asHttpHeaders()
					.forEach((key, value1) -> value1.forEach(value -> sb
						.append("\n")
						.append(key)
						.append(":")
						.append(value)));
				LOGGER.debug(sb.toString());
			}
			return Mono.just(clientResponse);
		});
	}
}