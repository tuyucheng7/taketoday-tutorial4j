package cn.tuyucheng.taketoday.sample.boundary;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.*;

@Configuration
public class CorsConfiguration {

   @Bean
   public WebMvcConfigurer corsConfigurer(final CorsConfigurationData allowed) {
      return new WebMvcConfigurer() {

         @Override
         public void addCorsMappings(final CorsRegistry registry) {
            registry.addMapping("/**")
                  .exposedHeaders(LOCATION, LINK)
                  // allow all HTTP request methods
                  .allowedMethods(stream(RequestMethod.values()).map(Enum::name).toArray(String[]::new)) //
                  // allow the commonly used headers
                  .allowedHeaders(ORIGIN, CONTENT_TYPE, CONTENT_LANGUAGE, ACCEPT, ACCEPT_LANGUAGE, IF_MATCH, IF_NONE_MATCH) //
                  // this is stage specific
                  .allowedOrigins(allowed.getOrigins())
                  .allowCredentials(allowed.isCredentials());
         }
      };
   }
}