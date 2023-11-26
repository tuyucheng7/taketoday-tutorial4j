package cn.tuyucheng.taketoday.metrics.filter;

import cn.tuyucheng.taketoday.metrics.service.CustomActuatorMetricService;
import cn.tuyucheng.taketoday.metrics.service.InMemoryMetricService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@Component
public class MetricFilter implements Filter {

   @Autowired
   private InMemoryMetricService metricService;

   @Autowired
   private CustomActuatorMetricService actMetricService;

   @Override
   public void init(final FilterConfig config) {
      if (metricService == null || actMetricService == null) {
         WebApplicationContext appContext = WebApplicationContextUtils
               .getRequiredWebApplicationContext(config.getServletContext());

         metricService = appContext.getBean(InMemoryMetricService.class);
         actMetricService = appContext.getBean(CustomActuatorMetricService.class);
      }
   }

   @Override
   public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws java.io.IOException, ServletException {
      final HttpServletRequest httpRequest = ((HttpServletRequest) request);
      final String req = httpRequest.getMethod() + " " + httpRequest.getRequestURI();

      chain.doFilter(request, response);

      final int status = ((HttpServletResponse) response).getStatus();
      metricService.increaseCount(req, status);
      actMetricService.increaseCount(status);
   }
}