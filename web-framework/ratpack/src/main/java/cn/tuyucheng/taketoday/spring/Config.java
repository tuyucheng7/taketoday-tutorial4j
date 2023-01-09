package cn.tuyucheng.taketoday.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class Config {

	@Bean
	public Content content() {
		return () -> "hello baeldung!";
	}

	@Bean
	public ArticleList articles() {
		return () -> Arrays.asList("Introduction to Ratpack", "Ratpack Google Guice Integration", "Ratpack Spring Boot Integration");
	}

}
