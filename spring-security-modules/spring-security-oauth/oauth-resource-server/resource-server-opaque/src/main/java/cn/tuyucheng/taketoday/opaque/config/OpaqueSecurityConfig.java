package cn.tuyucheng.taketoday.opaque.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class OpaqueSecurityConfig {

   @Value("${spring.security.oauth2.resourceserver.opaque.introspection-uri}")
   String introspectionUri;

   @Value("${spring.security.oauth2.resourceserver.opaque.introspection-client-id}")
   String clientId;

   @Value("${spring.security.oauth2.resourceserver.opaque.introspection-client-secret}")
   String clientSecret;

   @Bean
   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      http.authorizeRequests(authz -> authz.antMatchers(HttpMethod.GET, "/bars/**")
                  .hasAuthority("SCOPE_read")
                  .antMatchers(HttpMethod.POST, "/bars")
                  .hasAuthority("SCOPE_write")
                  .anyRequest()
                  .authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2.opaqueToken(token -> token.introspectionUri(this.introspectionUri)
                  .introspectionClientCredentials(this.clientId, this.clientSecret)));
      return http.build();
   }
}