package cn.tuyucheng.taketoday.jsonb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.JsonbHttpMessageConverter;

import java.util.ArrayList;
import java.util.Collection;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@ComponentScan(basePackages = {"cn.tuyucheng.taketoday.jsonb"})
public class Spring5Application {

	public static void main(String[] args) {
		SpringApplication.run(Spring5Application.class, args);
	}

	@Bean
	public HttpMessageConverters customConverters() {
		Collection<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
		JsonbHttpMessageConverter jsonbHttpMessageConverter = new JsonbHttpMessageConverter();
		messageConverters.add(jsonbHttpMessageConverter);
		return new HttpMessageConverters(true, messageConverters);
	}
}