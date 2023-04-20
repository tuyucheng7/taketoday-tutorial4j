package cn.tuyucheng.taketoday.junit5.mockito.repository;

import cn.tuyucheng.taketoday.junit5.mockito.User;

public interface MailClient {

	void sendUserRegistrationMail(User user);
}