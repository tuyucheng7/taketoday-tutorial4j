package cn.tuyucheng.taketoday.mockito.junit5.repository;

import cn.tuyucheng.taketoday.mockito.junit5.User;

public interface UserRepository {

   User insert(User user);

   boolean isUsernameAlreadyExists(String userName);
}