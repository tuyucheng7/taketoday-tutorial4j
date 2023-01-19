package cn.tuyucheng.taketoday.threadlocal;

import java.util.UUID;

public class UserRepository {

	String getUserNameForUserId(Integer userId) {
		return UUID.randomUUID().toString();
	}
}