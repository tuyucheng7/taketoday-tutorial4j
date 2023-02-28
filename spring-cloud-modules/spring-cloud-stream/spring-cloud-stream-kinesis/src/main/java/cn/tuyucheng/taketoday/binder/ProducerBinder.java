package cn.tuyucheng.taketoday.binder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;

import java.util.function.Supplier;
import java.util.stream.IntStream;

@Configuration
class ProducerBinder {

	@Bean
	public Supplier output() {
		return () -> IntStream.range(1, 200)
			.mapToObj(ipSuffix -> "192.168.0." + ipSuffix)
			.map(entry -> MessageBuilder.withPayload(entry)
				.build());
	}
}