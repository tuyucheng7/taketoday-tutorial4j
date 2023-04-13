package cn.tuyucheng.taketoday.properties.log;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentPropertiesPrinter {
	private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentPropertiesPrinter.class);
	private final Environment env;

	public EnvironmentPropertiesPrinter(Environment env) {
		this.env = env;
	}

	@PostConstruct
	public void logApplicationProperties() {
		LOGGER.info("************************* PROPERTIES(ENVIRONMENT) ******************************");

		LOGGER.info("{}={}", "tuyucheng.property", env.getProperty("tuyucheng.property"));
		LOGGER.info("{}={}", "app.name", env.getProperty("app.name"));
		LOGGER.info("{}={}", "app.description", env.getProperty("app.description"));

		LOGGER.info("******************************************************************************");
	}
}