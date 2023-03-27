package cn.tuyucheng.taketoday.lazy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Lazy
@Configuration
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.lazy")
public class AppConfig {

	@Lazy
	@Bean
	public Region getRegion() {
		return new Region();
	}

	@Bean
	public Country getCountry() {
		return new Country();
	}
}
