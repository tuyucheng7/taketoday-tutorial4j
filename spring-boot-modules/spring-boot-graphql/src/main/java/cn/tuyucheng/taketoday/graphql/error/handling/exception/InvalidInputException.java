package cn.tuyucheng.taketoday.graphql.error.handling.exception;

public class InvalidInputException extends RuntimeException {
	public InvalidInputException(String message) {
		super(message);
	}
}