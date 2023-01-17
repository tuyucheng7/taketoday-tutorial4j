package cn.tuyucheng.taketoday.pattern.cleanarchitecture.usercreation;

interface UserFactory {
	User create(String name, String password);
}
