package cn.tuyucheng.taketoday.opentelemetry.exception;

public class ProductNotFoundException extends RuntimeException {
	public ProductNotFoundException(String productNotFound) {
		super(productNotFound);
	}
}