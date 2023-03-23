package cn.tuyucheng.taketoday;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

@Slf4j
@SpringBootApplication
public class VirtualThreadApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(VirtualThreadApplication.class, args);
	}

	@Autowired
	ApplicationEventPublisher publisher;

	@Override
	public void run(String... args) throws Exception {
		Instant start = Instant.now();
		IntStream.rangeClosed(1, 10_000)
			.forEachOrdered(i -> publisher.publishEvent("Hello #" + i + " at:" + LocalDateTime.now()));

		Instant finish = Instant.now();
		long timeElapsed = Duration.between(start, finish).toMillis();

		LOGGER.info("=====Elapsed time : {} ========", timeElapsed);
	}
}