package cn.tuyucheng.taketoday.binder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class ConsumerBinder {
	@Bean
	Consumer<String> input() {
		return str -> {
			System.out.println(str);
		};
	}
}