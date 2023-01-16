package cn.tuyucheng.taketoday.spring;

import com.maxmind.geoip2.DatabaseReader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.web.context.request.RequestContextListener;

@Configuration
public class TestIntegrationConfig {

	@Bean
	@ConditionalOnMissingBean(RequestContextListener.class)
	public RequestContextListener requestContextListener() {

		return new RequestContextListener();
	}

	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	@MockBean
	private JavaMailSender javaMailSender;

	@MockBean(name = "GeoIPCountry")
	private DatabaseReader databaseReader;

}
