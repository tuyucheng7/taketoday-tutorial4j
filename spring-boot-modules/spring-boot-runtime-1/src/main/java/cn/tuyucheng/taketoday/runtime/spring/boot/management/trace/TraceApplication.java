package cn.tuyucheng.taketoday.runtime.spring.boot.management.trace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@Profile("logging")
@SpringBootApplication
public class TraceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TraceApplication.class);
	}
}