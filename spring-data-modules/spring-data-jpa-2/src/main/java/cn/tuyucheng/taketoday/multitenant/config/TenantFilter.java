package cn.tuyucheng.taketoday.multitenant.config;

import cn.tuyucheng.taketoday.multitenant.security.AuthenticationService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@Order(1)
class TenantFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
						 FilterChain chain) throws IOException, ServletException {

		String tenant = AuthenticationService.getTenant((HttpServletRequest) request);
		TenantContext.setCurrentTenant(tenant);

		try {
			chain.doFilter(request, response);
		} finally {
			TenantContext.setCurrentTenant("");
		}
	}
}