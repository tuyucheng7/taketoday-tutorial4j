package cn.tuyucheng.taketoday.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoggingFilterRegistrationBean implements Filter {

   private LoggingService loggingService;

   public LoggingFilterRegistrationBean(LoggingService loggingService) {
      this.loggingService = loggingService;
   }

   @Override
   public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
      HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
      loggingService.log(httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
      filterChain.doFilter(servletRequest, servletResponse);
   }
}