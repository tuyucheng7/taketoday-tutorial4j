package cn.tuyucheng.taketoday.cloud.openfeign.defaulterrorhandling.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

public class FeignConfig {

	@Bean
	Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}
}