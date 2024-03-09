package cn.tuyucheng.taketoday.lapsedlistener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.tuyucheng.taketoday.lapsedlistener.UserGenerator.generateUser;

public class LapsedListenerRunner {

   private static final Logger logger = LoggerFactory.getLogger(LapsedListenerRunner.class);
   private static final MovieQuoteService movieQuoteService = new MovieQuoteService();

   static {
      movieQuoteService.start();
   }

   public static void main(String[] args) {
      while (true) {
         User user = generateUser();
         logger.debug("{} logged in", user.getName());
         user.subscribe(movieQuoteService);
         userUsingService();
         logger.debug("{} logged out", user.getName());
      }
   }

   private static void userUsingService() {
      try {
         Thread.sleep(10);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }

}
