package cn.tuyucheng.taketoday.webclient.status.exception;

public class CustomServerErrorException extends Exception {
	public CustomServerErrorException(String message) {
		super(message);
	}
}