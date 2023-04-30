package cn.tuyucheng.taketoday.tasksservice.service;

public class UnknownTaskException extends RuntimeException {
	private final String id;

	public UnknownTaskException(final String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}