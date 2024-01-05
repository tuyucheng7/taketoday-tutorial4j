package cn.tuyucheng.taketoday.customlogouthandler.web;

import cn.tuyucheng.taketoday.customlogouthandler.services.UserCache;
import cn.tuyucheng.taketoday.customlogouthandler.user.UserUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class CustomLogoutHandler implements LogoutHandler {

   private final UserCache userCache;

   public CustomLogoutHandler(UserCache userCache) {
      this.userCache = userCache;
   }

   @Override
   public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
      String userName = UserUtils.getAuthenticatedUserName();
      userCache.evictUser(userName);
   }
}