package cn.tuyucheng.taketoday.graphql.error.handling.exception;

public class VehicleNotFoundException extends RuntimeException {

	public VehicleNotFoundException(String message) {
		super(message);
	}
}