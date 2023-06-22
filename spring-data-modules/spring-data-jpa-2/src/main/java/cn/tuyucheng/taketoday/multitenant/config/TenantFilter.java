package cn.tuyucheng.taketoday.multitenant.config;

import cn.tuyucheng.taketoday.multitenant.config.TenantContext;
import cn.tuyucheng.taketoday.multitenant.security.AuthenticationService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
class TenantFilter implements Filter {

   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      String tenant = AuthenticationService.getTenant((HttpServletRequest) request);
      TenantContext.setCurrentTenant(tenant);

      try {
         chain.doFilter(request, response);
      } finally {
         TenantContext.setCurrentTenant("");
      }
   }
}