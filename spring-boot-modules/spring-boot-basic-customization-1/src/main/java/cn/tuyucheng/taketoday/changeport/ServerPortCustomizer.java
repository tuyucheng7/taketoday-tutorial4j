package cn.tuyucheng.taketoday.changeport;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

//@Component
public class ServerPortCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

	@Override
	public void customize(ConfigurableWebServerFactory factory) {
		factory.setPort(8086);
	}
}