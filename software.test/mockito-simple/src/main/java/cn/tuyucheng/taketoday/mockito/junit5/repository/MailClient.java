package cn.tuyucheng.taketoday.mockito.junit5.repository;

import cn.tuyucheng.taketoday.mockito.junit5.User;

public interface MailClient {

   void sendUserRegistrationMail(User user);
}