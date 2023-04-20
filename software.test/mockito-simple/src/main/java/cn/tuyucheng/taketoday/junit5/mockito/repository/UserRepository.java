package cn.tuyucheng.taketoday.junit5.mockito.repository;

import cn.tuyucheng.taketoday.junit5.mockito.User;

public interface UserRepository {

	User insert(User user);

	boolean isUsernameAlreadyExists(String userName);
}