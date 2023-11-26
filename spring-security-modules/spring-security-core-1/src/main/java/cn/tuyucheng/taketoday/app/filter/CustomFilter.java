package cn.tuyucheng.taketoday.app.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component("loggingFilter")
public class CustomFilter implements Filter {

   private static Logger LOGGER = LoggerFactory.getLogger(CustomFilter.class);

   @Override
   public void init(FilterConfig config) throws ServletException {
   }

   @Override
   public void doFilter(ServletRequest request, ServletResponse response,
                        FilterChain chain) throws IOException, ServletException {
      HttpServletRequest req = (HttpServletRequest) request;
      LOGGER.info("Request Info : " + req);
      chain.doFilter(request, response);
   }

   @Override
   public void destroy() {
      // cleanup code, if necessary
   }
}
