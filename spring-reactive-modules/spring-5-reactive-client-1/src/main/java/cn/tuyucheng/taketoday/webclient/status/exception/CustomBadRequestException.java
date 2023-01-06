package cn.tuyucheng.taketoday.webclient.status.exception;

public class CustomBadRequestException extends Exception {
	public CustomBadRequestException(String message) {
		super(message);
	}
}