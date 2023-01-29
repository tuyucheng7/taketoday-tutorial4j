package cn.tuyucheng.taketoday.concurrent.threadsafety.services;

public class MessageService {

	private final String message;

	public MessageService(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}