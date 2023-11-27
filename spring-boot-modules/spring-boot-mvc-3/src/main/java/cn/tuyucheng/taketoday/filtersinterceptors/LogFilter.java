package cn.tuyucheng.taketoday.filtersinterceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

@Component
public class LogFilter implements Filter {

   private Logger logger = LoggerFactory.getLogger(LogFilter.class);

   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      logger.info(STR."Hello from: \{request.getLocalAddr()}");
      chain.doFilter(request, response);
   }
}