package cn.tuyucheng.taketoday.loginextrafieldscustom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@PropertySource("classpath:/application-extrafields.properties")
public class SecurityConfig extends AbstractHttpConfigurer<SecurityConfig, HttpSecurity> {

   @Autowired
   private CustomUserDetailsService userDetailsService;

   @Autowired
   private PasswordEncoder passwordEncoder;

   @Override
   public void configure(HttpSecurity http) throws Exception {
      AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
      http.addFilterBefore(authenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class);
   }

   public static SecurityConfig securityConfig() {
      return new SecurityConfig();
   }

   @Bean
   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      http.authorizeRequests()
            .antMatchers("/css/**", "/index")
            .permitAll()
            .antMatchers("/user/**")
            .authenticated()
            .and()
            .formLogin()
            .loginPage("/login")
            .and()
            .logout()
            .logoutUrl("/logout")
            .and()
            .apply(securityConfig());
      return http.build();
   }

   public CustomAuthenticationFilter authenticationFilter(AuthenticationManager authenticationManager) throws Exception {
      CustomAuthenticationFilter filter = new CustomAuthenticationFilter();
      filter.setAuthenticationManager(authenticationManager);
      filter.setAuthenticationFailureHandler(failureHandler());
      return filter;
   }

   @Autowired
   public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
      auth.authenticationProvider(authProvider());
   }

   public AuthenticationProvider authProvider() {
      CustomUserDetailsAuthenticationProvider provider
            = new CustomUserDetailsAuthenticationProvider(passwordEncoder, userDetailsService);
      return provider;
   }

   public SimpleUrlAuthenticationFailureHandler failureHandler() {
      return new SimpleUrlAuthenticationFailureHandler("/login?error=true");
   }

}
