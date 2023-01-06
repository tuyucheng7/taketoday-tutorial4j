package cn.tuyucheng.taketoday.reactive.exception;

public class ServiceException extends RuntimeException {

	public ServiceException(String message) {
		super(message);
	}
}