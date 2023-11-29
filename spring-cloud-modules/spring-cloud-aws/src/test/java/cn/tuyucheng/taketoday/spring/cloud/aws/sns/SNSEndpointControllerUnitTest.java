package cn.tuyucheng.taketoday.spring.cloud.aws.sns;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.aws.messaging.endpoint.NotificationStatus;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

class SNSEndpointControllerUnitTest {

   SNSEndpointController snsEndpointController;

   @BeforeEach
   void setUp() {
      snsEndpointController = new SNSEndpointController();
   }

   @Test
   void whenReceivedNotificationInvoked_thenSuccess() {
      snsEndpointController.receiveNotification("Message", "Subject");
   }

   @Test
   void whenConfirmUnsubscribeReturned_thenSuccess() {
      NotificationStatus notificationStatus = mock(NotificationStatus.class);
      doNothing().when(notificationStatus).confirmSubscription();
      snsEndpointController.confirmUnsubscribeMessage(notificationStatus);
   }

   @Test
   void whenConfirmSubscriptionReturned_thenSuccess() {
      NotificationStatus notificationStatus = mock(NotificationStatus.class);
      doNothing().when(notificationStatus).confirmSubscription();
      snsEndpointController.confirmSubscriptionMessage(notificationStatus);
   }
}