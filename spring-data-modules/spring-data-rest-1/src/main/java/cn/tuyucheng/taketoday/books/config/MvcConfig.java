package cn.tuyucheng.taketoday.books.config;

import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cn.tuyucheng.taketoday.books.events.AuthorEventHandler;
import cn.tuyucheng.taketoday.books.events.BookEventHandler;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {

	public MvcConfig() {
		super();
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Bean
	WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> enableDefaultServlet() {
		return factory -> factory.setRegisterDefaultServlet(true);
	}

	@Bean
	AuthorEventHandler authorEventHandler() {
		return new AuthorEventHandler();
	}

	@Bean
	BookEventHandler bookEventHandler() {
		return new BookEventHandler();
	}
}