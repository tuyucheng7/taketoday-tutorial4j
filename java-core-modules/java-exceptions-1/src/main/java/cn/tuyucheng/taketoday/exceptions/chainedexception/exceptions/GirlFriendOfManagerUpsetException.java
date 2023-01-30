package cn.tuyucheng.taketoday.exceptions.chainedexception.exceptions;

public class GirlFriendOfManagerUpsetException extends Exception {

	public GirlFriendOfManagerUpsetException(String message, Throwable cause) {
		super(message, cause);
	}

	public GirlFriendOfManagerUpsetException(String message) {
		super(message);
	}
}