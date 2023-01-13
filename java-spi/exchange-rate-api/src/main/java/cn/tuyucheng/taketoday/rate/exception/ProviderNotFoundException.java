package cn.tuyucheng.taketoday.rate.exception;

public class ProviderNotFoundException extends RuntimeException {

	public ProviderNotFoundException() {
		super();
	}

	public ProviderNotFoundException(String message) {
		super(message);
	}
}