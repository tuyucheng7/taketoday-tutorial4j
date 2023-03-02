package cn.tuyucheng.taketoday.opentelemetry.exception;

public class PriceNotFoundException extends RuntimeException {
	public PriceNotFoundException(String priceNotFound) {
		super(priceNotFound);
	}
}