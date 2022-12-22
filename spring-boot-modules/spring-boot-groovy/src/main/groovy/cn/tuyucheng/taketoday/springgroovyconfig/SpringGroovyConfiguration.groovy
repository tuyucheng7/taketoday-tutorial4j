package cn.tuyucheng.taketoday.springgroovyconfig

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SpringGroovyConfiguration {

	static void main(String[] args) {}

	@Bean
	List<String> fruits() {
		[
			'Apple',
			'Orange',
			'Banana',
			'Grapes'
		]
	}

	@Bean
	Map<Integer, String> rankings() {
		[1: 'Gold', 2: 'Silver', 3: 'Bronze']
	}
}