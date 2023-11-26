package cn.tuyucheng.taketoday.common.error;

import jakarta.servlet.Servlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;

public class SpringHelloServletRegistrationBean extends ServletRegistrationBean {

   public SpringHelloServletRegistrationBean() {
   }

   public SpringHelloServletRegistrationBean(Servlet servlet, String... urlMappings) {
      super(servlet, urlMappings);
   }
}