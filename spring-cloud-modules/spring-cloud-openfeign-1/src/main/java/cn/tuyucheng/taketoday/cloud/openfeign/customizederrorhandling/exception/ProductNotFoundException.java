package cn.tuyucheng.taketoday.cloud.openfeign.customizederrorhandling.exception;

public class ProductNotFoundException extends RuntimeException {

	public ProductNotFoundException(String message) {
		super(message);
	}
}
