package cn.tuyucheng.taketoday.swaggerkeycloak;

import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ExcludeFromJacocoGeneratedReport
public class KeycloakConfigResolverConfig {

	/*
	 * re-configure keycloak adapter for Spring Boot environment,
	 * i.e. to read config from application.yml
	 * (otherwise, we need a keycloak.json file)
	 */
	@Bean
	public KeycloakConfigResolver configResolver() {
		return new KeycloakSpringBootConfigResolver();
	}
}