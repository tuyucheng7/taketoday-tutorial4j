package cn.tuyucheng.taketoday.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.io.IOException;

@Component
public class LoggingFilterAutowiringSupport implements Filter {

   @Autowired
   LoggingService loggingService;

   @Override
   public void init(FilterConfig filterConfig) {
      SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
            filterConfig.getServletContext());
   }

   @Override
   public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
      HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
      loggingService.log(httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
      filterChain.doFilter(servletRequest, servletResponse);
   }
}