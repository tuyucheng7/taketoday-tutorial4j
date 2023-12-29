package cn.tuyucheng.taketoday.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * The Entry Point will not redirect to any sort of Login - it will return the 401
 */
@Component
public final class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

   @Override
   public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
   }
}