package cn.tuyucheng.taketoday.requestheader.config;

import cn.tuyucheng.taketoday.requestheader.interceptor.OperatorHolder;
import cn.tuyucheng.taketoday.requestheader.interceptor.OperatorInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class HeaderInterceptorConfig implements WebMvcConfigurer {

   @Override
   public void addInterceptors(final InterceptorRegistry registry) {
      registry.addInterceptor(operatorInterceptor());
   }

   @Bean
   public OperatorInterceptor operatorInterceptor() {
      return new OperatorInterceptor(operatorHolder());
   }

   @Bean
   @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
   public OperatorHolder operatorHolder() {
      return new OperatorHolder();
   }
}