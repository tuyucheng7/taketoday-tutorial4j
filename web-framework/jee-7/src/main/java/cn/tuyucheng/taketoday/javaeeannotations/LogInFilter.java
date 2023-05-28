package cn.tuyucheng.taketoday.javaeeannotations;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*@WebFilter(
  urlPatterns = "/bankAccount/*",
  filterName = "LogInFilter",
  description = "Filter all account transaction URLs"
  )*/
public class LogInFilter implements javax.servlet.Filter {

   public void init(FilterConfig filterConfig) throws ServletException {
   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      HttpServletRequest req = (HttpServletRequest) request;
      HttpServletResponse res = (HttpServletResponse) response;

      res.sendRedirect(req.getContextPath() + "/login.jsp");
      chain.doFilter(request, response);
   }

   public void destroy() {

   }

}
