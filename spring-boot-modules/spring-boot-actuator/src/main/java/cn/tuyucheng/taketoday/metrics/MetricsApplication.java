package cn.tuyucheng.taketoday.metrics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.context.request.RequestContextListener;

import javax.servlet.ServletContext;

@EnableScheduling
@ComponentScan("cn.tuyucheng.taketoday.metrics")
@SpringBootApplication
public class MetricsApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(MetricsApplication.class);
	}

	@Override
	public void onStartup(ServletContext sc) {
		// Manages the lifecycle of the root application context
		sc.addListener(new RequestContextListener());
	}

	public static void main(final String[] args) {
		// only load properties for this application
		System.setProperty("spring.config.location", "classpath:application-metrics.properties");
		SpringApplication.run(MetricsApplication.class, args);
	}
}