package cn.tuyucheng.taketoday.reactorbus.service;

import cn.tuyucheng.taketoday.reactorbus.domain.NotificationData;

public interface NotificationService {

	void initiateNotification(NotificationData notificationData) throws InterruptedException;
}