package cn.tuyucheng.taketoday.springsecurity;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {

	public SecurityWebApplicationInitializer() {
		super(SpringSecurityConfig.class);
	}
}
