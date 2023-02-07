package cn.tuyucheng.taketoday.reactorbus.service.impl;

import cn.tuyucheng.taketoday.reactorbus.domain.NotificationData;
import cn.tuyucheng.taketoday.reactorbus.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceimpl implements NotificationService {

	@Override
	public void initiateNotification(NotificationData notificationData) throws InterruptedException {
		System.out.println("Notification service started for Notification ID: " + notificationData.getId());
		Thread.sleep(5000);
		System.out.println("Notification service ended for Notification ID: " + notificationData.getId());
	}
}