package cn.tuyucheng.taketoday.pattern.cleanarchitecture.usercreation;

interface User {
	boolean passwordIsValid();

	String getName();

	String getPassword();
}
