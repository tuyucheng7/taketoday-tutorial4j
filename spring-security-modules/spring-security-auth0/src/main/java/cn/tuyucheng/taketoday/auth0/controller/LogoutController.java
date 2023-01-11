package cn.tuyucheng.taketoday.auth0.controller;

import cn.tuyucheng.taketoday.auth0.AuthConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class LogoutController implements LogoutSuccessHandler {

    @Autowired
    private AuthConfig config;

    @Override
    public void onLogoutSuccess(HttpServletRequest req, HttpServletResponse res, Authentication authentication) {
        if (req.getSession() != null) {
            req.getSession().invalidate();
        }
        String returnTo = config.getContextPath(req);
        String logoutUrl = config.getLogoutUrl() + "?client_id=" + config.getClientId() + "&returnTo=" + returnTo;
        try {
            res.sendRedirect(logoutUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
