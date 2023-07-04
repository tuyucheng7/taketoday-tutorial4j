package cn.tuyucheng.taketoday.lapsedlistener;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MovieQuoteServiceUnitTest {

   @Test
   void whenSubscribeToService_thenServiceHasOneSubscriber() {
      MovieQuoteService service = new MovieQuoteService();
      service.attach(UserGenerator.generateUser());
      assertEquals(1, service.numberOfSubscribers());
   }

   @Test
   void whenUnsubscribeFromService_thenServiceHasNoSubscribers() {
      MovieQuoteService service = new MovieQuoteService();
      User user = UserGenerator.generateUser();
      service.attach(user);
      service.detach(user);
      assertEquals(0, service.numberOfSubscribers());
   }
}