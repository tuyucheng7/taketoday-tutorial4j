package cn.tuyucheng.taketoday.session.web.config;

import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class MainWebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext sc) throws ServletException {
        sc.getSessionCookieConfig().setHttpOnly(true);
        sc.getSessionCookieConfig().setSecure(true);
    }

}
