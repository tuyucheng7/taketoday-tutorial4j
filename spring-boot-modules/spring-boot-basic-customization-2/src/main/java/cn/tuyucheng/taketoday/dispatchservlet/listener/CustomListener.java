package cn.tuyucheng.taketoday.dispatchservlet.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomListener implements ServletContextListener {

   Logger logger = LoggerFactory.getLogger(CustomListener.class);

   @Override
   public void contextInitialized(ServletContextEvent sce) {
      logger.info("CustomListener is initialized");
   }

   @Override
   public void contextDestroyed(ServletContextEvent sce) {
      logger.info("CustomListener is destroyed");
   }
}