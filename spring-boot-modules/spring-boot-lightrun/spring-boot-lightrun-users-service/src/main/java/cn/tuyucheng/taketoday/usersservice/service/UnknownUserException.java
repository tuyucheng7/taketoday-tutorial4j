package cn.tuyucheng.taketoday.usersservice.service;

public class UnknownUserException extends RuntimeException {
	private final String id;

	public UnknownUserException(final String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}